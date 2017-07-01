package scheduler

import model.AppInstanceLog
import org.quartz.{Job, JobDataMap, JobExecutionContext}
import play.api.Logger
import play.api.libs.json.{JsObject, Json}
import repo.AppInstanceRepository
import repo.AppStatusRepository.Status
import scheduler.CommandExecutor.{Command, CommandResponse}
import util.AppException.JobExecutionException
import util.Keyword.{AppSetting, JobData}
import util.Util.joinPath
import util.{GlobalContext, Keyword}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future, blocking}

/**
  * Created by chlr on 6/10/17.
  */
abstract class CommandJob extends Job {

  private val logger = Logger(this.getClass)
  protected val instanceRepository: AppInstanceRepository
  protected val processCache: ProcessCache
  protected val instanceId: String

  /**
    * execute
    * @param context
    */
  override def execute(context: JobExecutionContext): Unit = {
    throw new RuntimeException("dummy error")
    Await.result[Unit](run(context), Duration.Inf)
  }


  def run(context: JobExecutionContext): Future[Unit] = {
    val dataMap = context.getJobDetail.getJobDataMap
    val groupName = context.getJobDetail.getKey.getGroup
    val jobName = context.getJobDetail.getKey.getName
    val triggerId = if(context.getMergedJobDataMap.containsKey("manual"))
      None else Some(context.getTrigger.getKey.getName)
    instanceRepository.createInstance(instanceId, jobName, groupName, triggerId) flatMap {
      _ => Future(blocking(new CommandExecutor(buildCommand(dataMap), processCache).execute()))
    } flatMap {
      case CommandResponse(stdout, stderr, None) =>
        logger.info(s"instance $instanceId execution completed successfully")
        instanceRepository.endInstance(instanceId, Status.success, stdout, stderr, Some(0), None)
      case CommandResponse(stdout, stderr, Some(x: JobExecutionException)) =>
        logger.info(s"instance $instanceId execution failed with status code ${x.returnCode}")
        instanceRepository.endInstance(instanceId, Status.fail, stdout, stderr, Some(x.returnCode), Some(x.getMessage))
      case CommandResponse(stdout, stderr, Some(x)) =>
        logger.error(s"fatal error invoking instance $instanceId", x)
        instanceRepository.endInstance(instanceId, Status.error, stdout, stderr, Some(-1), Some(x.getMessage))
    } recoverWith {
      case th =>
        logger.warn(s"instance $instanceId failed with exception", th)
        instanceRepository.endInstance(
          instanceId,
          Status.error,
          AppInstanceLog(instanceId, Keyword.AppLog.stdout, None),
          AppInstanceLog(instanceId, Keyword.AppLog.stdout, None),
          Some(-1),
          Some(th.getMessage)
        )
    }
  }


  /**
    * build Command object
    * @param dataMap
    * @return
    */
  protected def buildCommand(dataMap: JobDataMap) = {
    Command(instanceId, dataMap.getString(JobData.command), getWorkingDir(dataMap),
      GlobalContext.application.configuration.getString(s"app.${AppSetting.tmpDir}")
        .map(joinPath(_, instanceId)).get, getEnvironment(dataMap))
  }


  /**
    * get working directory for job
    * @param dataMap
    * @return
    */
  protected def getWorkingDir(dataMap: JobDataMap) = {
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
  protected def getEnvironment(dataMap: JobDataMap) = {
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
