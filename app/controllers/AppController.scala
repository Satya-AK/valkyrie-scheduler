package controllers

import com.google.inject.Inject
import model.AppJob.jsonReader
import model.{AppJob, AppTrigger}
import play.api.libs.json.{JsArray, JsValue, Json}
import play.api.mvc.{Action, Controller}
import repo.{JobRepository, TriggerRepository}
import scheduler.Scheduler
import util.Util

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by chlr on 5/27/17.
  */
class AppController @Inject()(triggerRepository: TriggerRepository,
                              jobRepository: JobRepository,
                              scheduler: Scheduler) extends Controller {


  /**
    * create job
    * @return
    */
  def createJob: Action[JsValue] = Action.async(parse.json) {
    request =>
      for {
        appJob <- Util.parseJson[AppJob](request.body)
        _ <- scheduler.createJob(appJob)
      } yield Ok(Json.obj("success" -> "job created"))
  }

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
    * list jobs in group
    * @param groupName
    * @return
    */
  def listJobs(groupName: String) = Action.async {
    jobRepository.listJobs(groupName)
      .map(x => x.map(Json.toJson(_)))
      .map(x => x.foldLeft(JsArray()){ case (arr, data) => arr :+ data })
      .map(x => Ok(x))
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
    * fetch job in group
    * @param groupName
    * @param jobName
    * @return
    */
  def fetchJob(groupName: String, jobName: String) = Action.async {
    jobRepository.getJob(jobName, groupName)
      .map(x => Ok(Json.toJson(x)))
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
