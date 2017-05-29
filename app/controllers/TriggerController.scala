package controllers

import com.google.inject.Inject
import model.AppTrigger
import play.api.libs.json.{JsArray, JsValue, Json}
import play.api.mvc.{Action, Controller}
import repo.TriggerRepository
import scheduler.Scheduler
import util.Util
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by chlr on 5/29/17.
  */


class TriggerController @Inject()(triggerRepository: TriggerRepository,
                                  scheduler: Scheduler)
  extends Controller {

  /**
    * create trigger
    * @return
    */
  def createTrigger: Action[JsValue] = Action.async(parse.json) {
    request =>
      for {
        appTrigger <- Util.parseJson[AppTrigger](request.body)
        _ <- scheduler.createTrigger(appTrigger)
      } yield Ok(Json.obj("success" -> "trigger created"))
  }


  /**
    * list triggers in group
    * @param groupName
    * @return
    */
  def listTriggers(groupName: String) = Action.async {
    triggerRepository.listTriggers(groupName)
      .map(x => x.map(Json.toJson(_)))
      .map(x => x.foldLeft(JsArray()){ case (arr, data) => arr :+ data })
      .map(x => Ok(x))
  }

  /**
    * fetch trigger in group
    * @param groupName
    * @param jobName
    * @return
    */
  def fetchTrigger(groupName: String, jobName: String) = Action.async {
    triggerRepository.getTrigger(jobName, groupName)
      .map(x => Ok(Json.toJson(x)))
  }

}
