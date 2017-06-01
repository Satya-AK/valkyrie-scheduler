package controllers

import com.google.inject.Inject
import play.api.libs.json.Json
import play.api.libs.ws.WSClient
import play.api.mvc.{Action, Controller, Results}
import repo.AppInstanceRepository
import scheduler.ProcessCache

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by chlr on 5/31/17.
  */
class InstanceController @Inject() (instanceRepository: AppInstanceRepository,
                                    processCache: ProcessCache,
                                    ws: WSClient) extends Controller {


  /**
    * kill instance
    * @param instanceId
    * @return
    */
  def requestInstanceKill(instanceId: String) = Action {
    processCache.remove(instanceId) match {
      case Some(x) => x.destroyForcibly(); Ok(Json.obj("success" -> "process killed"))
      case None => BadRequest(Json.obj("failed" -> "process handler not found"))
    }
  }

  /**
    * request instance kill
    * @param instanceId
    * @return
    */
  def instanceKill(instanceId: String) = Action.async {
    instanceRepository.fetchInstance(instanceId) flatMap {
      x => ws
          .url(s"http://${x.agent}"+controllers.routes.InstanceController.requestInstanceKill(instanceId).path)
          .post(Results.EmptyContent())
    } flatMap {
      case x if x.status == 200 => Future.successful(Ok(Json.parse(x.body)))
      case x => Future.successful(InternalServerError(Json.parse(x.body)))
    }
  }

}
