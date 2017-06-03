package controllers

import com.google.inject.Inject
import model.AppJob
import model.AppJob.jsonReader
import play.api.libs.json.{JsArray, JsValue, Json}
import play.api.mvc.{Action, Controller}
import repo.{JobRepository, TriggerRepository}
import scheduler.Scheduler
import util.{ErrRecoveryAction, Util}

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by chlr on 5/27/17.
  */
class JobController @Inject()(triggerRepository: TriggerRepository,
                              jobRepository: JobRepository,
                              scheduler: Scheduler) extends Controller {


  /**
    * create job
    * @return
    */
  def createJob: Action[JsValue] = ErrRecoveryAction.async(parse.json) {
    request =>
      for {
        appJob <- Util.parseJson[AppJob](request.body)
        _ <- scheduler.createJob(appJob)
      } yield Ok(Json.obj("success" -> "job created"))
  }


  /**
    * list jobs in group
    * @param groupName
    * @return
    */
  def listJobs(groupName: String) = ErrRecoveryAction.async {
    jobRepository.listJobs(groupName)
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
  def fetchJob(groupName: String, jobName: String) = ErrRecoveryAction.async {
    jobRepository.getJob(jobName, groupName)
      .map(x => Ok(Json.toJson(x)))
  }



}
