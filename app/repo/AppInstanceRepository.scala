package repo

import com.google.inject.Inject
import model.{AppInstance, AppStatus}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile
import table.AppInstanceTable
import util.Util.{uuid, _}
import AppStatusRepository.Status
import play.api.Application
import util.AppException.EntityNotFoundException
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by chlr on 5/29/17.
  */

class AppInstanceRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider,
                                      protected val instanceTable: AppInstanceTable,
                                      protected val statusRepository: AppStatusRepository,
                                      protected val application: Application)
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
                     triggerName: Option[String]): Future[String] = {
    def query(status: AppStatus, instanceId: String) = instanceTable.table
      .filter(x => x.jobName === jobName && x.groupName === groupName)
      .sortBy(_.seqId.desc).map(_.seqId).result.headOption flatMap {
       x => instanceTable.table +=
         AppInstance(instanceId, groupName, jobName, triggerName, currentTimeStamp, None,
           x.getOrElse(0L)+1, status.id, 1, accessPoint)
    }
    for {
      status <- statusRepository.getStatusName(Status.running)
      _ <- db.run(query(status, instanceId).transactionally).map(_ => uuid)
    } yield instanceId
  }


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
    * @return
    */
  def endInstance(instanceId: String, status: String) = {
    for {
      statusId <- statusRepository.getStatusName(status).map(_.id)
      instance <- fetchInstance(instanceId)
      _ <- db.run(instanceTable.table.filter(_.instanceId === instanceId)
        .update(instance.copy(statusId = statusId, endTime = Some(currentTimeStamp))))
    } yield ()
  }




  /**
    *
    * @return
    */
  private def accessPoint = {
    s"$hostName:${application.configuration.getString("http.port")}"
  }


}
