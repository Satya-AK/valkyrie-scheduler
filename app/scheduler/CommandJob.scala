package scheduler


import java.sql.Timestamp
import java.util.Date
import model.AppInstance
import org.quartz.{Job, JobDataMap, JobExecutionContext}
import play.api.Logger
import play.api.libs.json.{JsObject, Json}
import repo.AppStatusRepository.Status
import scheduler.CommandExecutor.{Command, CommandResponse}
import scheduler.DBManager.DBConnection
import util.AppException.JobExecutionException
import util.Keyword.{AppSetting, JobData}
import util.Util.{joinPath, uuid}
import util.{GlobalContext, ServiceHelper}

/**
  * Created by chlr on 6/10/17.
  */
abstract class CommandJob extends Job {

  private val logger = Logger(this.getClass)
  protected val serviceHelper: ServiceHelper
  protected val processCache: ProcessCache
  protected val dBConnection: DBConnection

  /**
    * execute
    * @param context
    */
  override def execute(context: JobExecutionContext): Unit = {
    run(context)
  }


  def run(context: JobExecutionContext) = {
    val dataMap = context.getJobDetail.getJobDataMap
    val command = buildCommand(dataMap)
    val triggerId = if(context.getMergedJobDataMap.containsKey("manual"))
      None else Some(context.getTrigger.getKey.getName)
    val appInstance = AppInstance(command.instanceId,
      context.getJobDetail.getKey.getGroup,
      context.getJobDetail.getKey.getName,
      triggerId,
      new Timestamp(new Date().getTime),
      None, None, None, -1, Status.running, 1, serviceHelper.accessPoint)
    val dBManager = new DBManager(dBConnection)
    try {
      if (dataMap.containsKey("instance_id"))
        dBManager.restartInstance(appInstance)
      else
        dBManager.startInstance(appInstance)
      new CommandExecutor(buildCommand(dataMap), processCache).execute() match {
         case CommandResponse(stdout, stderr, None) =>
           logger.info(s"instance ${command.instanceId} execution completed successfully")
           dBManager.endInstance(appInstance.copy(endTime = Some(new Timestamp(new Date().getTime)),
             statusId=Status.success, returnCode=Some(0)), stdout.log, stderr.log)
         case CommandResponse(stdout, stderr, Some(x: JobExecutionException)) =>
           logger.info(s"instance ${command.instanceId} execution failed with status code ${x.returnCode}")
           dBManager.endInstance(appInstance.copy(endTime=Some(new Timestamp(new Date().getTime)),
             statusId=Status.fail, returnCode=Some(x.returnCode)), stdout.log, stderr.log)
         case CommandResponse(stdout, stderr, Some(x)) =>
           logger.error(s"fatal error invoking instance ${command.instanceId}", x)
           dBManager.endInstance(appInstance.copy(statusId=Status.error, returnCode=Some(-1),
             message = Some(x.getMessage)), stdout.log, stderr.log)
       }
    } catch {
      case th: Throwable =>
        logger.warn(s"instance ${command.instanceId} failed with exception", th)
        dBManager.endInstance(appInstance.copy(statusId=4, message = Some(th.getMessage), returnCode=Some(-1)), None, None)
    }
  }


  /**
    * build Command object
    * @param dataMap
    * @return
    */
  protected def buildCommand(dataMap: JobDataMap) = {
    val instanceId = Option(dataMap.getString("instance_id")).getOrElse(uuid)
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
