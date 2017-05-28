package scheduler

import com.google.inject.{Inject, Singleton}
import model.{AppJob, AppTrigger}
import org.quartz._
import org.quartz.impl.StdSchedulerFactory
import play.api.Application
import util.Util

import scala.collection.JavaConverters._
import scala.concurrent.Future

/**
  * Created by chlr on 5/27/17.
  */

@Singleton()
class Scheduler @Inject() (application: Application) {


  private lazy val scheduler = {
    val schedulerFactory = new StdSchedulerFactory()
    schedulerFactory.initialize(Util.configToProperties(application.configuration.getConfig("scheduler").get
      ,application.configuration))
    val _scheduler = schedulerFactory.getScheduler()
    _scheduler.start()
    _scheduler
  }

  def createJob(job: AppJob) = {
    val jobDetail = JobBuilder.newJob(classOf[CommandJob])
      .withIdentity(new JobKey(job.jobName, job.groupName))
      .setJobData(new JobDataMap(Map("command" -> job.cmd).asJava))
      .storeDurably(true)
      .build()
    Future.successful(scheduler.addJob(jobDetail, true))
  }

  def createTrigger(appTrigger: AppTrigger) = {
    val triggerDetail = TriggerBuilder
      .newTrigger()
      .withIdentity(new TriggerKey(appTrigger.triggerName, appTrigger.groupName))
      .forJob(appTrigger.jobName, appTrigger.groupName)
      .withSchedule(CronScheduleBuilder.cronSchedule(appTrigger.quartzCron).withMisfireHandlingInstructionDoNothing())
      .build()
    Future.successful(scheduler.scheduleJob(triggerDetail))
  }


}
