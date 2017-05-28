package controllers

import com.google.inject.Inject
import model.AppJob.jsonReader
import model.{AppJob, AppTrigger}
import play.api.libs.json.{JsArray, JsValue, Json}
import play.api.mvc.{Action, Controller}
import repo.DBRepository
import scheduler.Scheduler
import util.Util
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by chlr on 5/27/17.
  */
class AppController @Inject()(dBRepository: DBRepository,
                              scheduler: Scheduler) extends Controller {


  def createJob: Action[JsValue] = Action.async(parse.json) {
    request =>
      for {
        appJob <- Util.parseJson[AppJob](request.body)
        _ <- scheduler.createJob(appJob)
      } yield Ok(Json.obj("success" -> "job created"))
  }

  def createTrigger: Action[JsValue] = Action.async(parse.json) {
    request =>
      for {
        appTrigger <- Util.parseJson[AppTrigger](request.body)
        _ <- scheduler.createTrigger(appTrigger)
      } yield Ok(Json.obj("success" -> "trigger created"))
  }

  def listJobs(groupName: String) = Action.async {
    dBRepository.listJobs(groupName)
      .map(x => x.map(Json.toJson(_)))
      .map(x => x.foldLeft(JsArray()){ case (arr, data) => arr :+ data })
      .map(x => Ok(x))
  }

  def listTriggers(groupName: String) = Action.async {
    dBRepository.listJobs(groupName)
      .map(x => x.map(Json.toJson(_)))
      .map(x => x.foldLeft(JsArray()){ case (arr, data) => arr :+ data })
      .map(x => Ok(x))
  }

}
