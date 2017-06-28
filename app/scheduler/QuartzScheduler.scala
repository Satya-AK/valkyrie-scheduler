package scheduler

import java.io.File

import com.google.inject.{Inject, Singleton}
import model.{AppJob, AppTrigger}
import org.quartz._
import org.quartz.impl.StdSchedulerFactory
import play.api.{Application, Logger}
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

  def createJob(groupId: String, job: AppJob) = {
    val jobDetail = JobBuilder.newJob(classOf[CommandJobImpl])
      .withIdentity(new JobKey(job.jobName, groupId))
      .setJobData(new JobDataMap(Map("command" -> job.cmd).asJava))
      .storeDurably(true)
      .withDescription(job.desc.orNull)
      .build()
    Future.successful(scheduler.addJob(jobDetail, false))
  }

  def createTrigger(groupId: String, appTrigger: AppTrigger) = {
    println(("+"* 20) + appTrigger.jobName)
    val triggerDetail = TriggerBuilder
      .newTrigger()
      .withIdentity(new TriggerKey(appTrigger.triggerName, groupId))
      .forJob(appTrigger.jobName, groupId)
      .withDescription(appTrigger.desc.orNull)
      .withSchedule(CronScheduleBuilder.cronSchedule(appTrigger.quartzCron).withMisfireHandlingInstructionDoNothing())
      .build()
    Future.successful(scheduler.scheduleJob(triggerDetail)).map(_ => ())
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
