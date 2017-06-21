package controllers

import com.google.inject.Inject
import model.AppJob
import model.AppJob.jsonFormatter
import play.api.libs.json.{JsArray, Json}
import play.api.mvc.Controller
import repo.{AppGroupRepository, JobRepository, TriggerRepository}
import scheduler.Scheduler
import util.{ErrRecoveryAction, Util}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

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


  def listDummyJobs = ErrRecoveryAction.async {
    val list = Seq(AppJob("job_1", "echo Hello world", Some("this is testJob")),
      AppJob("job_2", "echo Hello world", Some("this is testJob")),
      AppJob("job_3", "echo Hello world", Some("this is testJob")),
      AppJob("job_4", "echo Hello world", Some("this is testJob")))
    Future.successful(Ok(Json.toJson(list)))
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
