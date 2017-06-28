package model

import play.api.libs.functional.syntax._
import play.api.libs.json.{Format, JsPath}
import scheduler.SchedulerParser

/**
  * Created by chlr on 5/28/17.
  */


/**
  *
  * @param triggerName
  * @param groupName
  * @param jobName
  * @param cron
  * @param desc
  */
case class AppTrigger(triggerName: String,
                      jobName: String,
                      cron: String,
                      desc: Option[String]) {

  def quartzCron = SchedulerParser.linuxToQuartz(cron)

}

object AppTrigger {

  implicit val jsonFormatter: Format[AppTrigger] = (
    (JsPath \ "name").format[String] and
    (JsPath \ "job_name").format[String] and
    (JsPath \ "cron").format[String] and
    (JsPath \ "description").formatNullable[String]
  )(AppTrigger.apply, unlift(AppTrigger.unapply))

  def create(trigger: Trigger, cronTrigger: CronTrigger) = {
    AppTrigger(trigger.triggerName, trigger.jobName, cronTrigger.linuxCron, trigger.desc)
  }

}
