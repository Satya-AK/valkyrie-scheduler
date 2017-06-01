package scheduler

import org.quartz.{Job, JobDataMap, JobExecutionContext}
import repo.AppInstanceRepository
import repo.AppStatusRepository.Status
import util.{GlobalContext, Keyword}
import util.Util._
import Keyword.{AppSetting, JobData}
import play.api.libs.json.{JsObject, Json}
import scheduler.CommandExecutor.Command

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, blocking}
import scala.util.{Failure, Success}

/**
  * Created by chlr on 5/27/17.
  */
class CommandJob extends Job {

  val instanceRepository = GlobalContext.injector.getInstance(classOf[AppInstanceRepository])
  val processCache = GlobalContext.injector.getInstance(classOf[ProcessCache])
  val instanceId = uuid

  /**
    * execute
    * @param context
    */
  override def execute(context: JobExecutionContext): Unit = {
    val dataMap = context.getJobDetail.getJobDataMap
    instanceRepository.createInstance(
      instanceId
      , context.getJobDetail.getKey.getGroup
      , context.getTrigger.getKey.getName
      , Some(context.getJobDetail.getKey.getName)) flatMap {
      _ => Future(blocking(new CommandExecutor(buildCommand(dataMap), processCache).execute()))
    } onComplete {
      case Success(_) => instanceRepository.endInstance(instanceId, Status.success)
      case Failure(x) => x.printStackTrace(); instanceRepository.endInstance(instanceId, Status.fail)
    }
  }


  /**
    * build Command object
    * @param dataMap
    * @return
    */
  def buildCommand(dataMap: JobDataMap) = {
    Command(instanceId, dataMap.getString(JobData.command), getWorkingDir(dataMap),
      GlobalContext.application.configuration
        .getString(s"app.${AppSetting.tmpDir}")
        .map(joinPath(_, instanceId)).get, getEnvironment(dataMap))
  }


  /**
    * get working directory for job
    * @param dataMap
    * @return
    */
  def getWorkingDir(dataMap: JobDataMap) = {
    if (dataMap.containsKey(JobData.workingDir))
      dataMap.getString(JobData.workingDir)
    else
      GlobalContext.application.path.getPath
  }

  /**
    * get environment setting for the job
    * @param dataMap
    * @return
    */
  def getEnvironment(dataMap: JobDataMap) = {
    if (dataMap.containsKey(JobData.environment)) {
      Json.parse(dataMap.getString(JobData.environment))
        .as[JsObject].value
        .map({ case (key, value) => key -> value.as[String] })
        .toMap
    }
    else
      Map[String, String]()
  }

}
