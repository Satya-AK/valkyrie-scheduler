package scheduler

import model.{AppJob, AppTrigger}

import scala.concurrent.Future

/**
  * Created by chlr on 6/4/17.
  */
trait Scheduler {

  /**
    * setup scheduler
    */
  def setup(): Unit

  /**
    * create job
    * @param job
    * @return
    */
  def createJob(job: AppJob): Future[Unit]

  /**
    * create trigger
    * @param trigger
    * @return
    */
  def createTrigger(trigger: AppTrigger): Future[Unit]


  /**
    * launch job
    * @param groupId
    * @param jobId
    * @return
    */
  def launchJob(groupId: String, jobId: String, instanceId: String): Future[Unit]

  /**
    * update job
    * @param job
    * @return
    */
  def updateJob(job: AppJob): Future[Unit]


  /**
    * update trigger
    * @param trigger
    * @return
    */
  def updateTrigger(trigger: AppTrigger): Future[Unit]

  /**
    * delete job in group
    * @param groupId
    * @param jobId
    * @return
    */
  def deleteJob(groupId: String, jobId: String): Future[Unit]


  /**
    * delete a trigger
    * @param groupId
    * @param jobId
    * @return
    */
  def deleteTrigger(groupId: String, jobId: String): Future[Unit]


  /**
    *
    * @param groupId
    * @param jobId
    * @return
    */
  def enableTrigger(groupId: String, jobId: String): Future[Unit]


  /**
    * disable trigger
    * @param groupId
    * @param triggerId
    * @return
    */
  def disableTrigger(groupId: String, triggerId: String): Future[Unit]


  /**
    * restart instance
    * @param instanceId
    * @return
    */
  def restartInstance(instanceId: String): Future[Unit]


  /**
    * put scheduler in standbydown
    * @return
    */
  def disableScheduler: Future[Unit]


  /**
    * enable scheduler
    * @return
    */
  def enabledScheduler: Future[Unit]


  /**
    * destroy the scheduler
    */
  def destroy(): Unit

  /**
    * stop the scheduler
    */
  def pause(): Unit

}
