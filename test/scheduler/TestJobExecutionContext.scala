package scheduler

import java.util.Date
import scala.collection.JavaConverters._
import org.quartz
import org.quartz._

/**
  * Created by chlr on 6/4/17.
  */
class TestJobExecutionContext(protected val jobName: String,
                              protected val groupId: String,
                              protected val triggerName: String,
                              protected val dataMap: Map[String, String]) extends JobExecutionContext {

  override def getFireInstanceId: String = ???

  override def setResult(result: scala.Any): Unit = ???

  override def getCalendar: Calendar = ???

  override def put(key: scala.Any, value: scala.Any): Unit = ???

  override def getMergedJobDataMap: JobDataMap = ???

  override def getJobRunTime: Long = ???

  override def get(key: scala.Any): AnyRef = ???

  override def getScheduler: quartz.Scheduler = ???

  override def getNextFireTime: Date = ???

  override def getTrigger: Trigger =  {
    new Trigger {
      override def compareTo(other: Trigger): Int = ???

      override def getJobDataMap: JobDataMap = ???

      override def getDescription: String = ???

      override def getEndTime: Date = ???

      override def mayFireAgain(): Boolean = ???

      override def getMisfireInstruction: Int = ???

      override def getJobKey: JobKey = ???

      override def getStartTime: Date = ???

      override def getNextFireTime: Date = ???

      override def getKey: TriggerKey = new TriggerKey(triggerName, groupId)

      override def getTriggerBuilder: TriggerBuilder[_ <: Trigger] = ???

      override def getPreviousFireTime: Date = ???

      override def getFinalFireTime: Date = ???

      override def getScheduleBuilder: ScheduleBuilder[_ <: Trigger] = ???

      override def getFireTimeAfter(afterTime: Date): Date = ???

      override def getCalendarName: String = ???

      override def getPriority: Int = ???
    }
  }

  override def getScheduledFireTime: Date = ???

  override def getFireTime: Date = ???

  override def getPreviousFireTime: Date = ???

  override def isRecovering: Boolean = ???

  override def getJobDetail: JobDetail = new JobDetail {

    override def getKey: JobKey = new JobKey(jobName, groupId)

    override def requestsRecovery(): Boolean = ???

    override def isDurable: Boolean = ???

    override def getJobDataMap: JobDataMap = new JobDataMap(dataMap.asJava)

    override def getDescription: String = ???

    override def isPersistJobDataAfterExecution: Boolean = ???

    override def getJobBuilder: JobBuilder = ???

    override def getJobClass: Class[_ <: Job] = ???

    override def isConcurrentExectionDisallowed: Boolean = ???
  }

  override def getResult: AnyRef = ???

  override def getRefireCount: Int = ???

  override def getRecoveringTriggerKey: TriggerKey = ???

  override def getJobInstance: Job = ???
}
