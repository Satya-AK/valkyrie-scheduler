package controllers

import com.google.inject.Inject
import model.AppTrigger
import play.api.libs.json.{JsArray, Json}
import play.api.mvc.Controller
import repo.{AppGroupRepository, TriggerRepository}
import scheduler.Scheduler
import util.{ErrRecoveryAction, Util}

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by chlr on 5/29/17.
  */


class TriggerController @Inject()(triggerRepository: TriggerRepository,
                                  groupRepository: AppGroupRepository,
                                  scheduler: Scheduler)
  extends Controller {

  /**
    * create trigger
    * @return
    */
  def createTrigger(groupName: String) = ErrRecoveryAction.async(parse.json) {
    request =>
      for {
        group <- groupRepository.getGroupByName(groupName)
        appTrigger <- Util.parseJson[AppTrigger](request.body)
        _ <- scheduler.createTrigger(group.id, appTrigger)
      } yield Ok(Json.obj("success" -> "trigger created"))
  }


  /**
    * list triggers in group
    * @param groupName
    * @return
    */
  def listTriggers(groupName: String) = ErrRecoveryAction.async {
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
  def fetchTrigger(groupName: String, jobName: String) = ErrRecoveryAction.async {
    triggerRepository.getTrigger(jobName, groupName)
      .map(x => Ok(Json.toJson(x)))
  }

}
