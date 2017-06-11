package repo

import com.google.inject.{Inject, Singleton}
import model.{AppInstance, AppInstanceLog, AppStatus}
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
                                      protected val appInstanceLogRepository: AppInstanceLogRepository,
                                      protected val serviceHelper: ServiceHelper)
  extends HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._



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
        .update(instance.copy(statusId = statusId, endTime = Some(currentTimeStamp))))
      _ <- appInstanceLogRepository.save(stdout)
      _ <- appInstanceLogRepository.save(stderr)
    } yield ()
  }

}
