package scheduler

import java.io.File

import com.google.inject.{Inject, Singleton}
import model.{AppJob, AppTrigger}
import org.quartz._
import org.quartz.impl.StdSchedulerFactory
import play.api.{Application, Logger}
import repo.AppInstanceRepository
import util.Keyword.JobData
import util.{GlobalContext, Util}

import scala.collection.JavaConverters._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by chlr on 5/27/17.
  */

@Singleton()
class QuartzScheduler @Inject() (application: Application,
                                 appInstanceRepository: AppInstanceRepository,
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


  def createJob(job: AppJob) = createOrUpdateJob(job)

  def updateJob(job: AppJob) = createOrUpdateJob(job, update=true)

  def createOrUpdateJob(job: AppJob, update: Boolean = false) = {
    val data = Map(JobData.command -> job.cmd,
      JobData.workingDir -> job.workingDir,
        JobData.jobName -> job.jobName)
    val jobDetail = JobBuilder.newJob(classOf[CommandJobImpl])
      .withIdentity(new JobKey(job.id, job.groupId))
      .setJobData(new JobDataMap(data.asJava))
      .storeDurably(true)
      .withDescription(job.desc.orNull)
      .build()
    Future.successful(scheduler.addJob(jobDetail, update))
  }

  def createTrigger(appTrigger: AppTrigger) = {
    val triggerDetail = TriggerBuilder
      .newTrigger()
      .forJob(appTrigger.jobId, appTrigger.groupId)
      .withIdentity(new TriggerKey(appTrigger.id, appTrigger.groupId))
      .usingJobData("name", appTrigger.triggerName)
      .withDescription(appTrigger.desc.orNull)
      .withSchedule(CronScheduleBuilder.cronSchedule(appTrigger.quartzCron).withMisfireHandlingInstructionDoNothing())
      .build()
    Future.successful(scheduler.scheduleJob(triggerDetail)).map(_ => ())
  }

  def updateTrigger(appTrigger: AppTrigger) = {
    val triggerKey = new TriggerKey(appTrigger.id, appTrigger.groupId)
    val triggerDetail = TriggerBuilder
      .newTrigger()
      .withIdentity(triggerKey)
      .forJob(appTrigger.jobId, appTrigger.groupId)
      .usingJobData("name", appTrigger.triggerName)
      .withDescription(appTrigger.desc.orNull)
      .withSchedule(CronScheduleBuilder.cronSchedule(appTrigger.quartzCron).withMisfireHandlingInstructionDoNothing())
      .build()
    Future.successful(scheduler.rescheduleJob(triggerKey, triggerDetail)).map(_ => ())
  }


  def launchJob(groupId: String, jobId: String) = {
    Future.successful(scheduler.triggerJob(new JobKey(jobId, groupId),
      new JobDataMap(Map("manual" -> "true").asJava))).map(_ => ())
  }

  def createOrUpdateTrigger(appTrigger: AppTrigger, replace: Boolean) = {
    val triggerDetail = TriggerBuilder
      .newTrigger()
      .withIdentity(new TriggerKey(appTrigger.id, appTrigger.groupId))
      .usingJobData("name", appTrigger.triggerName)
      .withDescription(appTrigger.desc.orNull)
      .withSchedule(CronScheduleBuilder.cronSchedule(appTrigger.quartzCron).withMisfireHandlingInstructionDoNothing())
      .build()
    val jobDetail = scheduler.getJobDetail(new JobKey(appTrigger.jobId, appTrigger.groupId))
    scheduler.scheduleJob(jobDetail, Seq(triggerDetail).toSet.asJava, replace)
    Future.successful(scheduler.scheduleJob(triggerDetail)).map(_ => ())
  }


  def disableTrigger(groupId: String, triggerId: String) = {
    val triggerKey = new TriggerKey(triggerId, groupId)
    Future.successful(scheduler.pauseTrigger(triggerKey))
  }

  def enableTrigger(groupId: String, triggerId: String) = {
    val triggerKey = new TriggerKey(triggerId, groupId)
    Future.successful(scheduler.resumeTrigger(triggerKey))
  }


  def deleteTrigger(groupId: String, triggerId: String) = {
    val triggerKey = new TriggerKey(triggerId, groupId)
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
    * @param jobId
    * @return
    */
  override def deleteJob(groupId: String, jobId: String): Future[Unit] = {
    val jobKey = new JobKey(jobId, groupId)
    Future.successful(scheduler.deleteJob(jobKey));
  }

  /**
    * restart instance
    *
    * @param instanceId
    * @return
    */
  override def restartInstance(instanceId: String): Future[Unit] = {
    for {
      instance <- appInstanceRepository.fetchInstance(instanceId)
      _ = scheduler.triggerJob(new JobKey(instance.jobId, instance.groupId),
        new JobDataMap(Map("instance_id" -> instanceId).asJava))
    } yield ()
  }
}
