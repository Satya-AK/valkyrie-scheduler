package model

import play.api.libs.functional.syntax._
import play.api.libs.json.{Format, JsPath}
import util.Keyword.JobData

/**
  * Created by chlr on 5/28/17.
  */


/**
  *
  * @param jobName
  * @param cmd
  * @param desc
  */
case class AppJob(jobName: String,
                  cmd: String,
                  desc: Option[String])

object AppJob {

  implicit val jsonFormatter: Format[AppJob] = (
    (JsPath \ "name").format[String] and
    (JsPath \ JobData.command).format[String] and
    (JsPath \ "desc").formatNullable[String]
  )(AppJob.apply, unlift(AppJob.unapply))

  def create(job: Job) = {
    AppJob(job.jobName, job.data.get(JobData.command), job.desc)
  }

}