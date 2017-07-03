package repo

import com.google.inject.{Inject, Singleton}
import model._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import repo.AppStatusRepository.Status
import slick.driver.JdbcProfile
import table.AppInstanceTable
import util.AppException.EntityNotFoundException
import util.ServiceHelper
import util.Util.{uuid, _}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by chlr on 5/29/17.
  */

@Singleton()
class AppInstanceRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider,
                                      protected val instanceTable: AppInstanceTable,
                                      protected val statusRepository: AppStatusRepository,
                                      protected val jobRepository: JobRepository,
                                      protected val appInstanceLogRepository: AppInstanceLogRepository,
                                      protected val serviceHelper: ServiceHelper)
  extends HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._


  /**
    * list instances of job
    * @param jobId
    * @return
    */
  def listInstances(jobId: String): Future[Seq[AppInstance]] = {
    db.run(instanceTable.table.filter(_.jobName === jobId).result)
  }

  /**
    *
    * @param jobName
    * @param groupName
    * @param triggerName
    */
  def createInstance(instanceId: String,
                     jobName: String,
                     groupName: String,
                     triggerName: Option[String]): Future[Unit] = {
    def query(status: AppStatus, instanceId: String) = instanceTable.table
      .filter(x => x.jobName === jobName && x.groupName === groupName)
      .sortBy(_.seqId.desc).map(_.seqId).result.headOption flatMap {
      x => instanceTable.table += AppInstance(instanceId, groupName, jobName, triggerName, currentTimeStamp, None,
        None, None, x.getOrElse(0L)+1, status.id, 1, serviceHelper.accessPoint)
    }
    for {
      status <- statusRepository.getStatusName(Status.running)
      _ <- db.run(query(status, instanceId).transactionally).map(_ => uuid)
    } yield ()
  }


  /**
    * fetch instance by instanceId
    * @param instanceId
    * @return
    */
  def fetchInstance(instanceId: String) = {
    db.run(instanceTable.table.filter(_.instanceId === instanceId).result.headOption) flatMap {
      case Some(x) => Future.successful(x)
      case None => Future.failed(new EntityNotFoundException(s"instance id $instanceId not found"))
    }
  }


  /**
    *
    * @param instanceId
    * @param status
    * @param stdout
    * @param stderr
    * @param retCode
    * @param message
    * @return
    */
  def endInstance(instanceId: String,
                  status: String,
                  stdout: AppInstanceLog,
                  stderr: AppInstanceLog,
                  retCode: Option[Int],
                  message: Option[String]) = {
    for {
      statusId <- statusRepository.getStatusName(status).map(_.id)
      instance <- fetchInstance(instanceId)
      _ <- db.run(instanceTable.table.filter(_.instanceId === instanceId)
        .update(instance.copy(statusId = statusId, endTime = Some(currentTimeStamp), returnCode=retCode,
          message=message)))
      _ <- appInstanceLogRepository.save(stdout)
      _ <- appInstanceLogRepository.save(stderr)
    } yield ()
  }


  /**
    * look up job by instanceId
    * @param instanceId
    * @return
    */
  def lookUpJobByInstance(instanceId: String): Future[AppJob] = {
    db.run(instanceTable.table.filter(_.instanceId === instanceId).result.headOption) flatMap {
      case Some(x) => Future.successful(x)
      case None => Future.failed(new EntityNotFoundException(s"instance-id $instanceId not found"))
    } flatMap {
      x => jobRepository.getJob(x.groupId, x.jobId)
    }
  }

  /**
    * instance query
    * @param instanceQuery
    * @return
    */
  def instanceQuery(instanceQuery: InstanceQuery): Future[Seq[AppInstance]] = {
    val query = instanceTable.table
      .filter(x => x.startTime.asColumnOf[java.sql.Date] > instanceQuery.startDate)
      .filter(x => x.endTime.asColumnOf[java.sql.Date] < instanceQuery.endDate)
    println(query.result.statements.mkString)
    instanceQuery.status match {
      case Some(x) => db.run(query.filter(_.statusId === x).result)
      case None => db.run(query.result)
    }
  }

}
