package scheduler

import java.sql._

import model._
import play.api.Logger
import repo.AppStatusRepository.Status
import scheduler.DBManager.DBConnection
import util.{GlobalContext, Keyword}

import scala.util.{Failure, Success, Try}

/**
  * Created by chlr on 7/3/17.
  */
class DBManager(protected val dBConnection: DBConnection) {

  val appInstanceTable = "APP_INSTANCE"
  val appInstanceLogTable = "APP_INSTANCE_LOG"
  val logger = Logger(this.getClass)

  def getConnection = {
    Class.forName(dBConnection.driver)
    val connection = DriverManager
      .getConnection(dBConnection.url, dBConnection.username, dBConnection.password)
     connection.setAutoCommit(false)
    connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED)
    connection
  }

  private def run(body: (Connection =>  Unit)) = {
    implicit val connection = getConnection
    val action = Try {
      body(connection)
    }
    action match {
      case Success(()) => connection.commit(); connection.close()
      case Failure(th) => connection.commit(); connection.close(); throw th
    }
  }

  def startInstance(appInstance: AppInstance): Unit = {
    run {
      implicit connection =>
      val seqId = this.getSeqId(appInstance)
      this.insertInstance(appInstance, seqId)
    }
  }

  def restartInstance(appInstance: AppInstance): Unit = {
      run {
        implicit connection =>
          restartInstanceUpdate(appInstance)
      }
  }

  /**
    *
    * @param appInstance
    * @param stdout
    * @param stderr
    */
  def endInstance(appInstance: AppInstance, stdout: Option[String], stderr: Option[String]) = {
   run {
     implicit connection =>
       this.updateInstance(appInstance)
      stdout.foreach(this.updateLog(appInstance, _, "stdout"))
      stderr.foreach(this.updateLog(appInstance, _, "stderr"))
      this.sendEmail(appInstance, stdout, stderr)
    }
  }


  /**
    * send email
    * @param appInstance
    */
  private def sendEmail(appInstance: AppInstance, stdout: Option[String], stderr: Option[String])
                       (implicit connection: Connection) = {
    GlobalContext.application.configuration
      .getConfig(Keyword.EmailSetting.rootKey)
      .map(AppEmailConnection.create) match {
      case None => logger.error(s"${Keyword.EmailSetting.rootKey} setting not found in app configuration")
      case Some(x) =>
        val emailManager = new EmailManager(x)
        val group = fetchGroup(appInstance.groupId)
        val job = fetchJob(appInstance.groupId, appInstance.jobId)
        if (job.emailOnFailure &&  Seq(Status.fail, Status.error).contains(appInstance.statusId) ||
          job.emailOnSuccess && appInstance.statusId == Status.success)
          emailManager.send(appInstance, job.jobName, group, stdout, stderr)
    }
  }



  private def getSeqId(appInstance: AppInstance)(implicit connection: Connection): Long = {
    val sql =
      s"""
         |SELECT max(SEQ_ID) FROM APP_INSTANCE WHERE GROUP_ID = '${appInstance.groupId}' AND
         |JOB_ID = '${appInstance.jobId}'
      """.stripMargin
    var rs: ResultSet = null
    var seqId: Long = 0
    Try {
      rs = connection.createStatement().executeQuery(sql)
      while(rs.next()) {
        seqId = rs.getLong(1)
      }
    } match {
      case Success(()) => if(rs != null) rs.close(); seqId
      case Failure(th) => if(rs != null) rs.close();  logger.error("error getting sequence id", th); 0
    }
  }

  private def insertInstance(appInstance: AppInstance, seqId: Long)(implicit connection: Connection): Unit = {
    val insertSql =
      s"""
         |INSERT INTO $appInstanceTable
         |  (INSTANCE_ID, GROUP_ID, JOB_ID, TRIGGER_ID, START_TIME, SEQ_ID, STATUS_ID, ATTEMPT, AGENT_NAME)
         |  VALUES
         |   (?, ?, ?, ?, ?, ?, ?, 1, ?);
      """.stripMargin
    val stmt = connection.prepareStatement(insertSql)
    stmt.setString(1, appInstance.id)
    stmt.setString(2, appInstance.groupId)
    stmt.setString(3, appInstance.jobId)
    appInstance.triggerId match {
      case Some(x) => stmt.setString(4, x)
      case None => stmt.setNull(4, java.sql.Types.VARCHAR)
    }
    stmt.setTimestamp(5, appInstance.startTime)
    stmt.setLong(6, seqId+1)
    stmt.setInt(7, appInstance.statusId)
    stmt.setString(8, appInstance.agent)
    stmt.execute()
  }

  /**
    * restart instance
    * @param appInstance
    * @param connection
    * @return
    */
  private def restartInstanceUpdate(appInstance: AppInstance)(implicit connection: Connection) = {
    val sql =
      s"""
        |UPDATE $appInstanceTable
        | SET
        |   START_TIME = ?,
        |   END_TIME = ?,
        |   RETURN_CODE = ?,
        |   STATUS_ID = ?
        | WHERE INSTANCE_ID = ?
      """.stripMargin
    val stmt = connection.prepareStatement(sql)
    stmt.setTimestamp(1, appInstance.startTime)
    stmt.setNull(2, java.sql.Types.TIMESTAMP)
    stmt.setNull(3, java.sql.Types.INTEGER)
    stmt.setInt(4, appInstance.statusId)
    stmt.setString(5, appInstance.id)
    stmt.execute()
  }


  private def updateInstance(appInstance: AppInstance)(implicit connection: Connection): Unit = {
    val sql =
      s"""
         |UPDATE $appInstanceTable
         | SET
         |   END_TIME = ?,
         |   MESSAGE = ?,
         |   RETURN_CODE = ?,
         |   STATUS_ID = ?
         | WHERE INSTANCE_ID = ?
       """.stripMargin
      val stmt = connection.prepareStatement(sql)
      appInstance.endTime match {
        case Some(x) => stmt.setTimestamp(1,x)
        case None => stmt.setNull(1, java.sql.Types.TIMESTAMP)
      }
    appInstance.message match {
      case Some(x) => stmt.setString(2, x.take(199))
      case None => stmt.setNull(2, java.sql.Types.VARCHAR)
    }
    appInstance.returnCode match {
      case Some(x) => stmt.setInt(3, x)
      case None => stmt.setNull(3, java.sql.Types.INTEGER)
    }
    stmt.setInt(4, appInstance.statusId)
    stmt.setString(5, appInstance.id)
    stmt.execute()
  }


  private def updateLog(appInstance: AppInstance, logData: String, logType: String)(implicit connection: Connection) = {
    val sql =
      s"""
         |INSERT INTO $appInstanceLogTable (INSTANCE_ID, LOG_TYPE, LOG_DATA) VALUES (?,?,?);
       """.stripMargin
    val stmt = connection.prepareStatement(sql)
    stmt.setString(1, appInstance.id)
    stmt.setString(2, logType)
    stmt.setString(3, logData)
    stmt.execute()
  }



  private def fetchGroup(groupId: String)(implicit connection: Connection) = {
    val sql ="""SELECT * FROM APP_GROUP WHERE ID = ?""".stripMargin
    var appGroup: AppGroup = null
    var rs: ResultSet = null
    var stmt: PreparedStatement = null
    try {
      stmt = connection.prepareStatement(sql)
      stmt.setString(1, groupId)
      rs = stmt.executeQuery()
      while (rs.next()) {
        appGroup = AppGroup(rs.getString(1), rs.getString(2), rs.getString(3), Option(rs.getString(4)))
      }
    } finally {
       Try({rs.close(); stmt.close()})
    }
    appGroup
  }


  private def fetchJob(groupId: String, jobId: String)(implicit connection: Connection) = {
    val sql ="""SELECT JOB_DATA,DESCRIPTION FROM QRTZ_JOB_DETAILS WHERE JOB_NAME = ? AND JOB_GROUP =?""".stripMargin
    var rs: ResultSet = null
    var stmt: PreparedStatement = null
    var job: Job = null
    try {
      stmt = connection.prepareStatement(sql)
      stmt.setString(1, jobId)
      stmt.setString(2, groupId)
      rs = stmt.executeQuery()
      while (rs.next()) {
        job = Job(jobId, groupId, Option(rs.getString(2)), Option(rs.getBlob(1)))
      }
    } finally {
      Try({rs.close(); stmt.close()})
    }
    AppJob.create(job)
  }
}

object DBManager {

  case class DBConnection(driver: String,
                          url: String,
                          username: String,
                          password: String)

}
