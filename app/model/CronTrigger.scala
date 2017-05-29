package model

import scheduler.SchedulerParser

/**
  * Created by chlr on 5/27/17.
  */

/**
  *
  * @param triggerName
  * @param triggerGroup
  * @param cron
  */
case class CronTrigger(triggerName: String,
                       triggerGroup: String,
                       cron: String) {
  def linuxCron = SchedulerParser.quartzToLinux(cron)
}
