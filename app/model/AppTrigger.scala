package model

import play.api.libs.functional.syntax._
import play.api.libs.json.{Format, JsPath}
import scheduler.SchedulerParser

/**
  * Created by chlr on 5/28/17.
  */


/**
  *
  * @param id
  * @param triggerName
  * @param jobId
  * @param cron
  * @param desc
  */
case class  AppTrigger(id: String,
                      triggerName: String,
                      groupId: String,
                      jobId: String,
                      disable: Boolean,
                      cron: String,
                      desc: Option[String]) {

  def quartzCron = SchedulerParser.linuxToQuartz(cron)

}

object AppTrigger {

  implicit val jsonFormatter: Format[AppTrigger] = (
    (JsPath \ "id").format[String] and
    (JsPath \ "name").format[String] and
    (JsPath \ "group_id").format[String] and
    (JsPath \ "job_id").format[String] and
    (JsPath \ "disable").format[Boolean] and
    (JsPath \ "cron").format[String] and
    (JsPath \ "description").formatNullable[String]
  )(AppTrigger.apply, unlift(AppTrigger.unapply))

  def create(trigger: Trigger, cronTrigger: CronTrigger) = {
    AppTrigger(trigger.triggerId ,trigger.data.get("name"), trigger.groupId, trigger.jobName,
      trigger.state == "PAUSED",cronTrigger.linuxCron, trigger.desc)
  }

}
