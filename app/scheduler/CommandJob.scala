package scheduler

import org.quartz.{Job, JobDataMap, JobExecutionContext}
import repo.InstanceRepository
import repo.StatusRepository.Status
import util.{GlobalContext, Keyword}
import util.Util._
import Keyword.{AppSetting, JobData}
import play.api.libs.json.{JsObject, Json}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, blocking}
import scala.util.{Failure, Success}

/**
  * Created by chlr on 5/27/17.
  */
class CommandJob extends Job {

  val instanceRepository = GlobalContext.injector.getInstance(classOf[InstanceRepository])

  /**
    * execute
    * @param context
    */
  override def execute(context: JobExecutionContext): Unit = {
    val instanceId = uuid
    val dataMap = context.getJobDetail.getJobDataMap
    instanceRepository.createInstance(
      instanceId
      , context.getJobDetail.getKey.getGroup
      , context.getTrigger.getKey.getName
      , Some(context.getJobDetail.getKey.getName)) flatMap {
      _ =>
        Future {
          blocking {
            new CommandExecutor(
              instanceId,
              dataMap.getString(JobData.command),
              getWorkingDir(dataMap),
              GlobalContext.application.configuration.getString(AppSetting.tmpDir).get,
              getEnvironment(dataMap)
            ).execute()
          }
        }
    } onComplete {
      case Success(_) => instanceRepository.endInstance(instanceId, Status.success)
      case Failure(_) => instanceRepository.endInstance(instanceId, Status.fail)
    }
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
