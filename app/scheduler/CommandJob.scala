package scheduler

import model.AppInstanceLog
import org.quartz.{Job, JobDataMap, JobExecutionContext}
import play.api.libs.json.{JsObject, Json}
import repo.AppStatusRepository.Status
import repo.{AppInstanceLogRepository, AppInstanceRepository}
import scheduler.CommandExecutor.{Command, CommandResponse}
import util.AppException.JobExecutionException
import util.{GlobalContext, Keyword}
import util.Keyword.{AppSetting, JobData}
import util.Util._

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
  val appInstanceLogRepository = GlobalContext.injector.getInstance(classOf[AppInstanceLogRepository])

  /**
    * execute
    * @param context
    */
  override def execute(context: JobExecutionContext): Unit = {
    val dataMap = context.getJobDetail.getJobDataMap
    val groupName = context.getJobDetail.getKey.getGroup
    val jobName = context.getJobDetail.getKey.getName
    val triggerName = Some(context.getTrigger.getKey.getName)
    instanceRepository.createInstance(instanceId, groupName, jobName, triggerName) flatMap {
      _ => Future(blocking(new CommandExecutor(buildCommand(dataMap), processCache).execute()))
    } onComplete {
      case Success(CommandResponse(stdout, stderr, None)) =>
        instanceRepository.endInstance(instanceId, Status.success, stdout, stderr)
      case Success(CommandResponse(stdout, stderr, Some(_: JobExecutionException))) =>
        instanceRepository.endInstance(instanceId, Status.fail, stdout, stderr)
      case Success(CommandResponse(stdout, stderr, Some(_))) =>
        instanceRepository.endInstance(instanceId, Status.error, stdout, stderr)
      case Failure(_) => instanceRepository.endInstance(
        instanceId,
        Status.error,
        AppInstanceLog(instanceId, Keyword.AppLog.stdout, None),
        AppInstanceLog(instanceId, Keyword.AppLog.stdout, None)
      )
    }
  }


  /**
    * build Command object
    * @param dataMap
    * @return
    */
  def buildCommand(dataMap: JobDataMap) = {
    Command(instanceId, dataMap.getString(JobData.command), getWorkingDir(dataMap),
      GlobalContext.application.configuration.getString(s"app.${AppSetting.tmpDir}")
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
