package scheduler

import java.io.File

import com.google.inject.Inject
import model.{AppJob, AppTrigger}
import org.quartz._
import org.quartz.impl.StdSchedulerFactory
import play.api.{Application, Logger}
import util.Util

import scala.collection.JavaConverters._
import scala.concurrent.Future

/**
  * Created by chlr on 5/27/17.
  */

class Scheduler @Inject() (application: Application) {

  val logger: Logger = Logger(this.getClass)

  createCacheDirectory()

  val scheduler = {
    val schedulerFactory = new StdSchedulerFactory()
    schedulerFactory.initialize(Util.configToProperties(application.configuration.getConfig("scheduler").get
      ,application.configuration))
    val _scheduler = schedulerFactory.getScheduler()
    _scheduler.start()
    logger.info("the scheduler has been started")
    _scheduler
  }


  /**
    * create the app directory
    */
  def createCacheDirectory() = {
    val tmpDirectory = new File(Util.appDirectory)
    if(tmpDirectory.exists()) {
      logger.info(s"app directory ${Util.appDirectory} already exists")
      require(tmpDirectory.canWrite, s"app directory ${Util.appDirectory} is not writeable")
    } else {
      logger.info(s"attempting to create app directory ${Util.appDirectory}")
      require(new File(Util.appDirectory).mkdirs(),
        s"failed to create app directory ${Util.appDirectory}")
    }
  }

  def createJob(job: AppJob) = {
    val jobDetail = JobBuilder.newJob(classOf[CommandJob])
      .withIdentity(new JobKey(job.jobName, job.groupName))
      .setJobData(new JobDataMap(Map("command" -> job.cmd).asJava))
      .storeDurably(true)
      .withDescription(job.desc.orNull)
      .build()
    Future.successful(scheduler.addJob(jobDetail, true))
  }

  def createTrigger(appTrigger: AppTrigger) = {
    val triggerDetail = TriggerBuilder
      .newTrigger()
      .withIdentity(new TriggerKey(appTrigger.triggerName, appTrigger.groupName))
      .forJob(appTrigger.jobName, appTrigger.groupName)
      .withDescription(appTrigger.desc.orNull)
      .withSchedule(CronScheduleBuilder.cronSchedule(appTrigger.quartzCron).withMisfireHandlingInstructionDoNothing())
      .build()
    Future.successful(scheduler.scheduleJob(triggerDetail))
  }


}
