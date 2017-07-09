package controllers

import com.google.inject.Inject
import play.api.libs.json.{JsArray, Json}
import play.api.mvc.Controller
import repo.SchedulerStateRepository
import scheduler.Scheduler
import util.ErrRecoveryAction
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by chlr on 7/9/17.
  */
class SchedulerController @Inject() (scheduler: Scheduler,
                                     schedulerStateRepository: SchedulerStateRepository)
  extends Controller {


  /**
    * get agents
    * @return
    */
  def agents = ErrRecoveryAction.async {
    schedulerStateRepository.getAgent
      .map(x => Ok(x.map(_.json).foldLeft(JsArray())({ case (arr,node) => arr :+ node })))
  }


  def setSchedulerState(state: Boolean) = ErrRecoveryAction.async {
    if(state)
      scheduler.enabledScheduler.map(_ => Ok(Json.obj("message" -> "scheduler has been enabled")))
    else
      scheduler.disableScheduler.map(_ => Ok(Json.obj("message" -> "scheduler has been disabled")))
  }


}
