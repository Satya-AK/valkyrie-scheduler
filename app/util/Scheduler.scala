package util

import com.google.inject.{Inject, Singleton}
import model.{CronTrigger, Job, Trigger}
import org.quartz._
import org.quartz.impl.StdSchedulerFactory
import play.api.Application

import scala.collection.JavaConverters._

/**
  * Created by chlr on 5/27/17.
  */

@Singleton()
class Scheduler @Inject() (application: Application) {

  private val scheduler = {
    val schedulerFactory = new StdSchedulerFactory()
    schedulerFactory.initialize(Util.configToProperties(application.configuration.getConfig("scheduler").get))
    schedulerFactory.getScheduler()
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
      .withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(5).repeatForever())
      .build()
    scheduler.scheduleJob(triggerDetail)
  }

}
