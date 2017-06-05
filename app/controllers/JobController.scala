package controllers

import com.google.inject.Inject
import model.AppJob
import model.AppJob.jsonReader
import play.api.libs.json.{JsArray, Json}
import play.api.mvc.Controller
import repo.{AppGroupRepository, JobRepository, TriggerRepository}
import scheduler.Scheduler
import util.{ErrRecoveryAction, Util}
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by chlr on 5/27/17.
  */
class JobController @Inject()(triggerRepository: TriggerRepository,
                              jobRepository: JobRepository,
                              groupRepository: AppGroupRepository,
                              scheduler: Scheduler) extends Controller {

  /**
    * create job
    * @return
    */
  def createJob(groupName: String) = ErrRecoveryAction.async(parse.json) {
    request =>
      for {
        group <- groupRepository.getGroupByName(groupName)
        appJob <- Util.parseJson[AppJob](request.body)
        _ <- scheduler.createJob(group.id, appJob)
      } yield Ok(Json.obj("success" -> "job created"))
  }


  /**
    * list jobs in group
    * @param groupName
    * @return
    */
  def listJobs(groupName: String) = ErrRecoveryAction.async {
    for {
      group <- groupRepository.getGroupByName(groupName)
      jobs <- jobRepository.listJobs(group.id)
    } yield {
      Ok(jobs.map(Json.toJson(_)).foldLeft(JsArray())({ case (arr, data) => arr :+ data }))
    }
  }



  /**
    * fetch job in group
    * @param groupName
    * @param jobName
    * @return
    */
  def fetchJob(groupName: String, jobName: String) = ErrRecoveryAction.async {
    for {
      group <- groupRepository.getGroupByName(groupName)
      job <- jobRepository.getJob(group.id, jobName)
    } yield {
      Ok(Json.toJson(job))
    }
  }



}
