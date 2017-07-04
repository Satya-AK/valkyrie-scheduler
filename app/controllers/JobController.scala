package controllers

import com.google.inject.Inject
import model.AppJob
import model.AppJob.jsonFormatter
import play.api.libs.json.{JsArray, JsObject, Json}
import play.api.mvc.Controller
import repo.{AppGroupRepository, AppInstanceRepository, JobRepository, TriggerRepository}
import scheduler.Scheduler
import util.{ErrRecoveryAction, Util}
import util.Util.{JsObjectEnhancer, uuid}
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by chlr on 5/27/17.
  */
class JobController @Inject()(triggerRepository: TriggerRepository,
                              jobRepository: JobRepository,
                              appInstanceRepository: AppInstanceRepository,
                              groupRepository: AppGroupRepository,
                              scheduler: Scheduler) extends Controller {

  /**
    * create job
    * @return
    */
  def createJob(groupId: String) = ErrRecoveryAction.async(parse.json) {
    request =>
      for {
        _ <- groupRepository.getGroupById(groupId)
        appJob <- Util.parseJson[AppJob](request.body.as[JsObject].update("group_id" -> groupId))
        _ <- scheduler.createJob(appJob)
      } yield Ok(Json.obj("message" -> "job created"))
  }

  /**
    * update the job
    * @param groupId
    * @return
    */
  def updateJob(groupId: String) = ErrRecoveryAction.async(parse.json) {
    request =>
      for {
        _ <- groupRepository.getGroupById(groupId)
        appJob <- Util.parseJson[AppJob](request.body.as[JsObject].update("group_id" -> groupId))
        _ <- scheduler.updateJob(appJob)
      } yield Ok(Json.obj("message" -> s"updated job ${appJob.jobName}"))
  }


  /**
    * delete job
    * @param groupId
    * @param jobId
    * @return
    */
  def deleteJob(groupId: String, jobId: String) = ErrRecoveryAction.async {
    for {
      group <- groupRepository.getGroupById(groupId)
      job <- jobRepository.getJob(group.id, jobId)
      _ <- scheduler.deleteJob(group.id, jobId)
    } yield Ok(Json.obj("message" -> s"job ${job.jobName} in group ${group.groupName} deleted successfully"))
  }


  /**
    * list jobs in group
    * @param groupId
    * @return
    */
  def listJobs(groupId: String) = ErrRecoveryAction.async {
    for {
      _ <- groupRepository.getGroupById(groupId)
      jobs <- jobRepository.listJobs(groupId)
    } yield {
      Ok(jobs.map(Json.toJson(_)).foldLeft(JsArray())({ case (arr, data) => arr :+ data }))
    }
  }


  /**
    * fetch job in group
    * @param groupId
    * @param jobId
    * @return
    */
  def fetchJob(groupId: String, jobId: String) = ErrRecoveryAction.async {
    for {
      group <- groupRepository.getGroupById(groupId)
      job <- jobRepository.getJob(group.id, jobId)
    } yield {
      Ok(Json.toJson(job))
    }
  }


  /**
    * launch job manually
    * @param groupId
    * @param jobId
    * @return
    */
  def launch(groupId: String, jobId: String) = ErrRecoveryAction.async {
    for {
      group <- groupRepository.getGroupById(groupId)
      job <- jobRepository.getJob(groupId, jobId)
      instanceId = uuid
      _ <- scheduler.launchJob(groupId, jobId, instanceId)
    } yield Ok(Json.obj("message" -> s"job ${job.jobName} in group ${group.groupName} triggered successfully"))
  }

}
