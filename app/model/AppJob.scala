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
case class AppJob(id: String,
                  jobName: String,
                  desc: Option[String],
                  cmd: String,
                  workingDir: String)

object AppJob {

  implicit val jsonFormatter: Format[AppJob] = (
    (JsPath \ "id").format[String] and
    (JsPath \ "name").format[String] and
    (JsPath \ "desc").formatNullable[String] and
    (JsPath \ JobData.command).format[String] and
    (JsPath \ JobData.workingDir).format[String]
  )(AppJob.apply, unlift(AppJob.unapply))

  def create(job: Job) = {
    AppJob(job.jobId,
      job.data.get(JobData.jobName),
      job.desc,
      job.data.get(JobData.command),
      job.data.get(JobData.workingDir))
  }

}