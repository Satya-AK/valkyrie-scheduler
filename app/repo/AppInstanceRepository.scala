package repo

import model.{AppInstance, AppInstanceLog}

import scala.concurrent.Future

/**
  * Created by chlr on 6/10/17.
  */
trait AppInstanceRepository {


  def createInstance(instanceId: String,
                     jobName: String,
                     groupName: String,
                     triggerName: Option[String]): Future[Unit]


  def fetchInstance(instanceId: String): Future[AppInstance]


  def endInstance(instanceId: String, status: String, stdout: AppInstanceLog, stderr: AppInstanceLog): Future[Unit]


}
