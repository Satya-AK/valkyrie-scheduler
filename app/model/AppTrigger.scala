package model

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Reads, Writes}
import scheduler.CronParser

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
                      groupName: String,
                      jobName: String,
                      cron: String,
                      desc: Option[String]) {

  def quartzCron = CronParser.linuxToQuartz(cron)

}

object AppTrigger {

  implicit val jsonWriter: Writes[AppTrigger] = (
      (JsPath \ "trigger_name").write[String] and
      (JsPath \ "group_name").write[String] and
      (JsPath \ "job_name").write[String] and
      (JsPath \ "cron").write[String] and
      (JsPath \ "description").writeNullable[String]
    )(unlift(AppTrigger.unapply))

  implicit val jsonReader: Reads[AppTrigger] = (
      (JsPath \ "trigger_name").read[String] and
      (JsPath \ "group_name").read[String] and
      (JsPath \ "job_name").read[String] and
      (JsPath \ "cron").read[String] and
      (JsPath \ "description").readNullable[String]
    )(AppTrigger.apply _)

  def create(trigger: Trigger, cronTrigger: CronTrigger) = {
    AppTrigger(trigger.triggerName, trigger.groupName, trigger.jobName, cronTrigger.linuxCron, trigger.desc)
  }

}
