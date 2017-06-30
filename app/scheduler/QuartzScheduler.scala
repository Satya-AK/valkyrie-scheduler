package scheduler

import java.io.File

import com.google.inject.{Inject, Singleton}
import model.{AppJob, AppTrigger}
import org.quartz._
import org.quartz.impl.StdSchedulerFactory
import play.api.{Application, Logger}
import util.Keyword.JobData
import util.{GlobalContext, Util}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.collection.JavaConverters._
import scala.concurrent.Future

/**
  * Created by chlr on 5/27/17.
  */

@Singleton()
class QuartzScheduler @Inject() (application: Application,
                                 globalContext: GlobalContext) extends Scheduler {

  val logger: Logger = Logger(this.getClass)

  val scheduler = {
    val schedulerFactory = new StdSchedulerFactory()
    schedulerFactory.initialize(Util.configToProperties(application.configuration.getConfig("scheduler").get
      ,application.configuration))
    val _scheduler = schedulerFactory.getScheduler()
    logger.info("the scheduler has been started")
    _scheduler
  }

  setup()

  def setup() = {
    createCacheDirectory()
    scheduler.start()
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


  def createJob(groupId: String, job: AppJob) = createOrUpdateJob(groupId, job)

  def updateJob(groupId: String, job: AppJob) = createOrUpdateJob(groupId, job, update=true)

  def createOrUpdateJob(groupId: String, job: AppJob, update: Boolean = false) = {
    val data = Map(JobData.command -> job.cmd,
      JobData.workingDir -> job.workingDir,
        JobData.jobName -> job.jobName)
    val jobDetail = JobBuilder.newJob(classOf[CommandJobImpl])
      .withIdentity(new JobKey(job.id, groupId))
      .setJobData(new JobDataMap(data.asJava))
      .storeDurably(true)
      .withDescription(job.desc.orNull)
      .build()
    Future.successful(scheduler.addJob(jobDetail, update))
  }

  def createTrigger(groupId: String, appTrigger: AppTrigger) = {
    val triggerDetail = TriggerBuilder
      .newTrigger()
      .forJob(appTrigger.jobId, groupId)
      .withIdentity(new TriggerKey(appTrigger.id, groupId))
      .usingJobData("name", appTrigger.triggerName)
      .withDescription(appTrigger.desc.orNull)
      .withSchedule(CronScheduleBuilder.cronSchedule(appTrigger.quartzCron).withMisfireHandlingInstructionDoNothing())
      .build()
    Future.successful(scheduler.scheduleJob(triggerDetail)).map(_ => ())
  }

  def updateTrigger(groupId: String, appTrigger: AppTrigger) = {
    val triggerKey = new TriggerKey(appTrigger.id, groupId)
    val triggerDetail = TriggerBuilder
      .newTrigger()
      .withIdentity(triggerKey)
      .forJob(appTrigger.jobId, groupId)
      .usingJobData("name", appTrigger.triggerName)
      .withDescription(appTrigger.desc.orNull)
      .withSchedule(CronScheduleBuilder.cronSchedule(appTrigger.quartzCron).withMisfireHandlingInstructionDoNothing())
      .build()
    Future.successful(scheduler.rescheduleJob(triggerKey, triggerDetail)).map(_ => ())
  }


  def createOrUpdateTrigger(groupId: String, appTrigger: AppTrigger, replace: Boolean) = {
    val triggerDetail = TriggerBuilder
      .newTrigger()
      .withIdentity(new TriggerKey(appTrigger.id, groupId))
      .usingJobData("name", appTrigger.triggerName)
      .withDescription(appTrigger.desc.orNull)
      .withSchedule(CronScheduleBuilder.cronSchedule(appTrigger.quartzCron).withMisfireHandlingInstructionDoNothing())
      .build()
    val jobDetail = scheduler.getJobDetail(new JobKey(appTrigger.jobId, groupId))
    scheduler.scheduleJob(jobDetail, Seq(triggerDetail).toSet.asJava, replace)
    Future.successful(scheduler.scheduleJob(triggerDetail)).map(_ => ())
  }


  def disableTrigger(groupId: String, triggerName: String) = {
    val triggerKey = new TriggerKey(triggerName, groupId)
    scheduler.pauseTrigger(triggerKey)
  }


  def deleteTrigger(groupId: String, triggerName: String) = {
    val triggerKey = new TriggerKey(triggerName, groupId)
    Future.successful(scheduler.unscheduleJob(triggerKey))
  }


  def pause() = {
    scheduler.standby()
  }

  def destroy() = {
    scheduler.shutdown()
  }

  /**
    * delete job in group
    *
    * @param groupId
    * @param jobName
    * @return
    */
  override def deleteJob(groupId: String, jobName: String): Future[Unit] = {
    val jobKey = new JobKey(jobName, groupId)
    Future.successful(scheduler.deleteJob(jobKey));
  }
}
