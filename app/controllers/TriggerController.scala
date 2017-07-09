package controllers

import com.google.inject.Inject
import model.AppTrigger
import play.api.libs.json.{JsArray, JsObject, Json}
import play.api.mvc.Controller
import repo.{AppGroupRepository, JobRepository, TriggerRepository}
import scheduler.Scheduler
import util.{ErrRecoveryAction, Util}
import util.Util.JsObjectEnhancer
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by chlr on 5/29/17.
  */


class TriggerController @Inject()(triggerRepository: TriggerRepository,
                                  jobRepository: JobRepository,
                                  groupRepository: AppGroupRepository,
                                  scheduler: Scheduler)
  extends Controller {

  /**
    * create trigger
    * @return
    */
  def createTrigger(groupId: String) = ErrRecoveryAction.async(parse.json) {
    request =>
      for {
        _ <- groupRepository.getGroupById(groupId)
        appTrigger <- Util.parseJson[AppTrigger](request.body.as[JsObject].update("group_id" -> groupId))
        _ <- triggerRepository.checkTriggerNameForCreate(groupId, appTrigger.triggerName)
        _ <- scheduler.createTrigger(appTrigger)
      } yield Ok(Json.obj("message" -> "trigger created"))
  }


  /**
    * update trigger
    * @param groupId
    * @return
    */
  def updateTrigger(groupId: String) = ErrRecoveryAction.async(parse.json) {
    request =>
      for {
        _ <- groupRepository.getGroupById(groupId)
        appTrigger <- Util.parseJson[AppTrigger](request.body.as[JsObject].update("group_id" -> groupId))
        _ <- triggerRepository.checkTriggerNameForUpdate(groupId, appTrigger.id ,appTrigger.triggerName)
        _ <- scheduler.updateTrigger(appTrigger)
      } yield Ok(Json.obj("message" -> "trigger updated successfully"))
  }


  /**
    * list triggers in group
    * @param groupId
    * @return
    */
  def listTriggers(groupId: String) = ErrRecoveryAction.async {
    for {
      group <- groupRepository.getGroupById(groupId)
      triggers <- triggerRepository.listTriggers(group.id)
      jobs <- Future.sequence(triggers.map(x => jobRepository.getJob(groupId, x.jobId)))
    } yield {
      Ok {
        (triggers zip jobs)
          .map({ case (x,y) => Json.toJson(x).as[JsObject].update("job_name" -> y.jobName)})
          .foldLeft(JsArray())({ case (arr, data) => arr :+ data })
      }
    }
  }

  /**
    * fetch trigger in group
    * @param groupId
    * @param triggerId
    * @return
    */
  def fetchTrigger(groupId: String, triggerId: String) = ErrRecoveryAction.async {
    for {
      group <- groupRepository.getGroupById(groupId)
      trigger <- triggerRepository.getTrigger(group.id, triggerId)
    } yield Ok(Json.toJson(trigger))
  }


  /**
    * disable trigger
    * @param groupId
    * @param triggerId
    * @return
    */
  def disable(groupId: String, triggerId: String) = ErrRecoveryAction.async {
    for {
      trigger <- triggerRepository.getTrigger(groupId, triggerId)
      _ <- scheduler.disableTrigger(groupId, triggerId)
    } yield Ok(Json.obj("success" -> s"trigger ${trigger.triggerName} disabled successfully"))
  }


  def enable(groupId: String, triggerId: String) = ErrRecoveryAction.async {
    for {
      trigger <- triggerRepository.getTrigger(groupId, triggerId)
      _ <- scheduler.enableTrigger(groupId, triggerId)
    } yield Ok(Json.obj("success" -> s"trigger ${trigger.triggerName} disabled successfully"))
  }


  /**
    * delete a trigger
    * @param groupId
    * @param triggerId
    * @return
    */
  def deleteTrigger(groupId: String, triggerId: String) = ErrRecoveryAction.async {
    for {
      _ <- groupRepository.getGroupByName(groupId)
      trigger <- scheduler.deleteTrigger(groupId, triggerId)
    } yield Ok(Json.obj("message" -> "trigger deleted successfully"))
  }

}
