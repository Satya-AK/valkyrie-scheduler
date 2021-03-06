package repo

import java.sql.Timestamp
import java.util.Date

import com.google.inject.{Inject, Singleton}
import model._
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import repo.AppStatusRepository.Status
import slick.driver.JdbcProfile
import table.AppInstanceTable
import util.AppException.{EntityNotFoundException, InvalidStateException}
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
    db.run(instanceTable.table.filter(_.jobId === jobId).result)
  }

  /**
    *
    * @param jobId
    * @param groupId
    * @param triggerId
    */
  def createInstance(instanceId: String,
                     jobId: String,
                     groupId: String,
                     triggerId: Option[String]): Future[Unit] = {
    def query(status: AppStatus, instanceId: String) = instanceTable.table
      .filter(x => x.jobId === jobId && x.groupId === groupId)
      .sortBy(_.seqId.desc).map(_.seqId).result.headOption flatMap {
      x => instanceTable.table += AppInstance(instanceId, groupId, jobId, triggerId, currentTimeStamp, None,
        None, None, x.getOrElse(0L)+1, status.id, 1, serviceHelper.accessPoint)
    }
    for {
      status <- statusRepository.getStatusById(Status.running)
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
    * fetch instance for restart
    * @param instanceId
    * @return
    */
  def fetchInstanceForRestart(instanceId: String) = {
    db.run(instanceTable.table
      .filter(x => x.instanceId === instanceId && x.statusId =!= Status.running).result.headOption) flatMap {
      case Some(x) => Future.successful(x)
      case None => Future.failed(new EntityNotFoundException(s"instance with id $instanceId cannot be restarted"))
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
      .filter(x => x.startTime.asColumnOf[java.sql.Date] >= instanceQuery.startDate)
      .filter(x => x.endTime.asColumnOf[java.sql.Date] <= instanceQuery.endDate)
      .filter(x => x.groupId === instanceQuery.groupId)
    instanceQuery.status match {
      case Some(x) => db.run(query.filter(_.statusId === x).result)
      case None => db.run(query.result)
    }
  }


  /**
    * force finish instance
    * @param instanceId
    * @return
    */
  def forceFinish(instanceId: String) = {
    db.run(instanceTable
      .table.filter(x => x.instanceId === instanceId && x.statusId =!= Status.finished).result.headOption) flatMap {
      case Some(x) => db.run(instanceTable
        .table.update(x.copy(statusId = Status.finished, endTime = Some(new Timestamp(new Date().getTime)),
        message = Some("Instance is forced"))))
      case None => Future.failed(new InvalidStateException(s"cannot force finish instance with id $instanceId"))
    }
  }

}
