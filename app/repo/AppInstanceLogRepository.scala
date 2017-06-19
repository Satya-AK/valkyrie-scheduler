package repo

import java.io.File

import com.google.inject.Inject
import model.AppInstanceLog
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile
import table.AppInstanceLogTable
import util.Util

import scala.concurrent.ExecutionContext.Implicits.global
import scala.io.Source

/**
  * Created by chlr on 5/31/17.
  */
class AppInstanceLogRepository @Inject() (protected val dbConfigProvider: DatabaseConfigProvider,
                                          appInstanceLogTable: AppInstanceLogTable)
  extends HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._

  /**
    * save instance log in database
    * @param appInstanceLog
    * @return
    */
  def save(appInstanceLog: AppInstanceLog) = {
    db.run(appInstanceLogTable.table += appInstanceLog)
  }

  def save(data: Seq[AppInstanceLog]) = {
    db.run(appInstanceLogTable.table ++= data)
  }

  /**
    * fetch instance log
    * @param instanceId
    * @param logType
    * @return
    */
  def fetch(instanceId: String, logType: String) = {
    db.run(appInstanceLogTable
      .table.filter(x => x.instanceId === instanceId && x.logType === logType)
      .map(_.logData).result.headOption
    ) map {
      case Some(x) => x
      case None =>
        val file = new File(Util.instanceLogDirectory(instanceId), s"$logType.log")
        if(file.exists())
          Some(Source.fromFile(file).mkString)
        else
          None
    }
  }



}
