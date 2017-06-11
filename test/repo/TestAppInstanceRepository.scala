package repo

import java.sql.Timestamp
import java.util.Date
import model.{AppInstance, AppInstanceLog}
import play.api.db.slick.DatabaseConfigProvider
import util.AppException.EntityNotFoundException
import scala.collection.mutable
import scala.concurrent.Future

/**
  * Created by chlr on 6/10/17.
  */
class TestAppInstanceRepository(dbConfigProvider: DatabaseConfigProvider)
  extends AppInstanceRepository(dbConfigProvider, null, null, null, null) {

  private val data: mutable.Map[String, AppInstance] = mutable.Map()

  override def createInstance(instanceId: String,
                              jobName: String,
                              groupName: String,
                              triggerName: Option[String]): Future[Unit] = {
      val instance = AppInstance(instanceId,
        groupName,
        jobName,
        triggerName,
        new Timestamp(new Date().getTime),
        None,
        1L,
        1,
        1,
        util.Util.hostName)
    Future.successful(data += (instanceId -> instance))
  }

  override def fetchInstance(instanceId: String): Future[AppInstance] = {
    data.get(instanceId) match {
      case Some(x) => Future.successful(x)
      case None => Future.failed(new EntityNotFoundException(s"instance $instanceId not found"))
    }
  }

  override def endInstance(instanceId: String, status: String, stdout: AppInstanceLog, stderr: AppInstanceLog) = {
    Future.successful(data += (instanceId -> data(instanceId).copy(statusId=2)))
  }
}
