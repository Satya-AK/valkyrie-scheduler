package controllers

import com.google.inject.Inject
import model.{CronTrigger, Job, Trigger}
import play.api.mvc.{Action, Controller}
import repo.DBRepository
import scheduler.chlr.test.Scheduler

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by chlr on 5/27/17.
  */
class JobController @Inject() (dBRepository: DBRepository,
                               scheduler: Scheduler) extends Controller {


  def createJob = Action.async {
    val jobDetail = scheduler.createJob(Job("faizah", "group", None, None))
    scheduler.createTrigger(Trigger("tgr_faizah", "group", "faizah", "group", None, None, None),
      CronTrigger("tgr_faizah", "group", ""), jobDetail)
    Future.successful(Ok(""))
  }

  def getJobInfo = Action.async {
    dBRepository.getJob("faizah", "group").foreach(x => println(x.data.get.mkString(",")))
    Future.successful(Ok(""))
  }

}
