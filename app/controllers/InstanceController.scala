package controllers

import com.google.inject.Inject
import model.InstanceQuery
import play.api.libs.json.{JsArray, JsObject, Json}
import play.api.mvc.Controller
import repo._
import scheduler.InstanceAction.{FetchLogAction, KillAction}
import scheduler.{ProcessCache, Scheduler}
import util.Util.JsObjectEnhancer
import util.{ErrRecoveryAction, Keyword, ServiceHelper, Util}
import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by chlr on 5/31/17.
  */
class InstanceController @Inject() (instanceRepository: AppInstanceRepository,
                                    appInstanceLogRepository: AppInstanceLogRepository,
                                    groupRepository: AppGroupRepository,
                                    triggerRepository: TriggerRepository,
                                    appStatusRepository: AppStatusRepository,
                                    jobRepository: JobRepository,
                                    processCache: ProcessCache,
                                    scheduler: Scheduler,
                                    serviceHelper: ServiceHelper) extends Controller {


  /**
    * list instances
    * @param jobId
    * @return
    */
  def list(jobId: String) = ErrRecoveryAction.async {
    instanceRepository.listInstances(jobId)
      .map(x => x.map(Json.toJson(_)).foldLeft(JsArray())({ case (acc,node) => acc :+ node }))
      .map(x => Ok(x))
  }

  /**
    * fetch instance by id
    * @param instanceId
    * @return
    */
  def fetch(instanceId: String) = ErrRecoveryAction.async {
    for {
      instance <- instanceRepository.fetchInstance(instanceId)
      job <- jobRepository.getJob(instance.groupId, instance.jobId)
      trigger <- instance.triggerId match {
        case Some(x) => triggerRepository.getTrigger(job.groupId, x).map(Some(_))
        case None => Future.successful(None)
      }
    } yield {
      Ok {
        Json.toJson(instance).as[JsObject]
          .update("job_name" -> job.jobName, "trigger_name" -> trigger.map(_.triggerName))
      }
    }
  }


  /**
    * kill instance
    * @param instanceId
    * @return
    */
  def requestInstanceKill(instanceId: String) = ErrRecoveryAction {
    processCache.remove(instanceId) match {
      case Some(x) => x.destroyProcess(); Ok(Json.obj("success" -> "process killed"))
      case None => BadRequest(Json.obj("failed" -> s"process handler not found for instance id $instanceId"))
    }
  }

  /**
    * request instance kill
    * @param instanceId
    * @return
    */
  def instanceKill(instanceId: String) = ErrRecoveryAction.async {
    instanceRepository.fetchInstance(instanceId) flatMap {
      x => serviceHelper.requestAction(KillAction(instanceId, x.agent), None)
    } map {
        _ => Ok(Json.obj("message" -> s"killed instance with id $instanceId successfully"))
    }
  }

  /**
    * request instance log
    * @param instanceId
    * @return
    */
  def requestShowLog(instanceId: String) = ErrRecoveryAction.async {
    for {
      stdout <- appInstanceLogRepository.fetch(instanceId, Keyword.AppLog.stdout)
      stderr <- appInstanceLogRepository.fetch(instanceId, Keyword.AppLog.stderr)
    } yield {
      Ok(Json.obj(Keyword.AppLog.stdout -> stdout, Keyword.AppLog.stderr -> stderr))
    }
  }

  /**
    * show instance log
    * @param instanceId
    * @return
    */
  def showLog(instanceId: String) = ErrRecoveryAction.async {
    instanceRepository.fetchInstance(instanceId) flatMap {
      x => serviceHelper.requestAction(FetchLogAction(instanceId, x.agent), None)
    } map {x =>  Ok(x) }
  }

  /**
    * lookup job by instanceId
    * @param instanceId
    * @return
    */
  def lookupJobByInstance(instanceId: String) = ErrRecoveryAction.async {
    instanceRepository.lookUpJobByInstance(instanceId)
      .map(x => Ok(Json.toJson(x)))
  }

  /**
    * query instances
    * @return
    */
  def queryInstances = ErrRecoveryAction.async(parse.json) {
    request =>
      println()
      for {
        instanceQuery <- Util.parseJson[InstanceQuery](request.body)
        instances <- instanceRepository.instanceQuery(instanceQuery)
      } yield {
        Ok(instances.map(x => Json.toJson(x)).foldLeft(JsArray())({ case (arr,node) => arr :+ node }))
      }
  }

  /**
    * list status
    * @return
    */
  def status = ErrRecoveryAction.async {
    appStatusRepository.list
      .map(x => x.map(x => Json.toJson(x)).foldLeft(JsArray())({case (arr,node) => arr :+ node}))
      .map(y => Ok(y))
  }

  /**
    * force finish instance
    * @param instanceId
    * @return
    */
  def forceFinish(instanceId: String) = ErrRecoveryAction.async {
    instanceRepository
      .forceFinish(instanceId)
      .map(_ => Ok(Json.obj("message" -> s"instance with id $instanceId has been force finished")))
  }


  def restartInstance(instanceId: String) = ErrRecoveryAction.async {
    for {
      _ <- instanceRepository.fetchInstanceForRestart(instanceId)
      _ <- scheduler.restartInstance(instanceId)
    } yield {
      Ok(Json.obj("message" -> s"instance $instanceId restarted successfully"))
    }
  }


  def test = ErrRecoveryAction.async {
    Future.successful(Ok(Json.toJson(processCache.cache.entrySet().asScala.map(_.getKey))))
  }

}
