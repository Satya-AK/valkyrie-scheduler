package controllers

import com.google.inject.Inject
import play.api.libs.json.Json
import play.api.mvc.Controller
import repo.{AppInstanceLogRepository, AppInstanceRepository}
import scheduler.InstanceAction.{FetchLogAction, KillAction}
import scheduler.ProcessCache
import util.{ErrRecoveryAction, Keyword, ServiceHelper}

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by chlr on 5/31/17.
  */
class InstanceController @Inject() (instanceRepository: AppInstanceRepository,
                                    appInstanceLogRepository: AppInstanceLogRepository,
                                    processCache: ProcessCache,
                                    serviceHelper: ServiceHelper) extends Controller {


  /**
    * kill instance
    * @param instanceId
    * @return
    */
  def requestInstanceKill(instanceId: String) = ErrRecoveryAction {
    processCache.remove(instanceId) match {
      case Some(x) => x.destroyForcibly(); Ok(Json.obj("success" -> "process killed"))
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
        _ => Ok(Json.obj("success" -> s"killed instance with id $instanceId successfully"))
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

}
