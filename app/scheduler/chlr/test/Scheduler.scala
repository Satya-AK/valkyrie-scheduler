package scheduler.chlr.test

import com.google.inject.{Inject, Singleton}
import model.{CronTrigger, Job, Trigger}
import org.quartz._
import org.quartz.impl.StdSchedulerFactory
import play.api.Application
import util.Util

import scala.collection.JavaConverters._

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

  def createJob(job: Job) = {
    val jobDetail = JobBuilder.newJob(classOf[CommandJob])
      .withIdentity(new JobKey(job.jobName, job.jobGroup))
      .setJobData(new JobDataMap(Map("foo" -> "bar", "Hello" -> "World").asJava))
      .storeDurably(true)
      .build()
    scheduler.addJob(jobDetail, true)
    jobDetail
  }

  def createTrigger(trigger: Trigger, cronTrigger: CronTrigger, job: JobDetail) = {
    val triggerDetail = TriggerBuilder
      .newTrigger()
      .withIdentity(new TriggerKey(trigger.triggerName, trigger.groupName))
      .forJob(job)
      .withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(2).repeatForever())
      .build()
    scheduler.scheduleJob(triggerDetail)
  }


}
