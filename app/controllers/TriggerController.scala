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
        group <- groupRepository.getGroupById(groupId)
        appTrigger <- Util.parseJson[AppTrigger](request.body)
        _ <- scheduler.createTrigger(group.id, appTrigger)
      } yield Ok(Json.obj("success" -> "trigger created"))
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
      trigger <- triggerRepository.getTrigger(triggerId, group.id)
    } yield Ok(Json.toJson(trigger))
  }


  /**
    * delete a trigger
    * @param groupName
    * @param jobName
    * @return
    */
  def deleteTrigger(groupName: String, jobName: String) = ErrRecoveryAction.async {
    for {
      group <- groupRepository.getGroupByName(groupName)
      trigger <- scheduler.deleteTrigger(group.id, jobName)
    } yield Ok(Json.obj("message" -> "trigger deleted successfully"))
  }

}
