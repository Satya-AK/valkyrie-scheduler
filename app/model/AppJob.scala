package model

import play.api.libs.json.{JsPath, Reads, Writes}
import play.api.libs.functional.syntax._


/**
  * Created by chlr on 5/28/17.
  */


/**
  *
  * @param jobName
  * @param groupName
  * @param cmd
  * @param desc
  */
case class AppJob(jobName: String,
                  groupName: String,
                  cmd: String,
                  desc: Option[String])

object AppJob {

  implicit val jsonWriter: Writes[AppJob] = (
    (JsPath \ "job_name").write[String] and
    (JsPath \ "group_name").write[String] and
    (JsPath \ "cmd").write[String] and
    (JsPath \ "description").writeNullable[String]
  )(unlift(AppJob.unapply))

  implicit val jsonReader: Reads[AppJob] = (
    (JsPath \ "job_name").read[String] and
    (JsPath \ "group_name").read[String] and
    (JsPath \ "cmd").read[String] and
    (JsPath \ "description").readNullable[String]
  )(AppJob.apply _)


  def create(job: Job) = {
    AppJob(job.jobName, job.jobGroup, job.data.get("command"), job.desc)
  }

}