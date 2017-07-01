package model

import util.Util.TimestampJsonParser
import java.sql.Timestamp
import play.api.libs.functional.syntax._
import play.api.libs.json._


/**
  * Created by chlr on 5/28/17.
  */

case class AppInstance(id: String,
                       groupId: String,
                       jobId: String,
                       triggerId: Option[String],
                       startTime: Timestamp,
                       endTime: Option[Timestamp],
                       message: Option[String],
                       returnCode: Option[Int],
                       seqId: Long,
                       statusId: Int,
                       attempt: Int,
                       agent: String)

object AppInstance {

  def tupled = (AppInstance.apply _).tupled

  implicit val formatter: Format[AppInstance] = (
    (__ \ "id").format[String] and
    (__ \ "group_id").format[String] and
    (__ \ "job_id").format[String] and
    (__ \ "trigger_name").formatNullable[String] and
    (__ \ "start_time").format[Timestamp] and
    (__ \ "end_time").formatNullable[Timestamp] and
    (__ \ "message").formatNullable[String] and
    (__ \ "return_code").formatNullable[Int] and
    (__ \ "seq_id").format[Long] and
    (__ \ "status").format[Int] and
    (__ \ "attempt").format[Int] and
    (__ \ "agent").format[String]
  )(AppInstance.apply, unlift(AppInstance.unapply))

}
