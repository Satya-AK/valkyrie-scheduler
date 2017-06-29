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
    * @param groupId
    * @param job
    * @return
    */
  def createJob(groupId: String, job: AppJob): Future[Unit]

  /**
    * create trigger
    * @param groupId
    * @param trigger
    * @return
    */
  def createTrigger(groupId: String, trigger: AppTrigger): Future[Unit]


  /**
    * update job
    * @param groupId
    * @param job
    * @return
    */
  def updateJob(groupId: String, job: AppJob): Future[Unit]


  /**
    * delete job in group
    * @param groupId
    * @param jobName
    * @return
    */
  def deleteJob(groupId: String, jobName: String): Future[Unit]


  /**
    * delete a trigger
    * @param groupId
    * @param jobName
    * @return
    */
  def deleteTrigger(groupId: String, jobName: String): Future[Unit]

  /**
    * destroy the scheduler
    */
  def destroy(): Unit

  /**
    * stop the scheduler
    */
  def pause(): Unit

}
