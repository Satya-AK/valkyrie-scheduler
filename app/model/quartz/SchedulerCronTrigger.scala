package model.quartz

/**
  * Created by chlr on 5/27/17.
  */

/**
  *
  * @param triggerName
  * @param triggerGroup
  * @param cron
  */
case class SchedulerCronTrigger(triggerName: String,
                                triggerGroup: String,
                                cron: String)
