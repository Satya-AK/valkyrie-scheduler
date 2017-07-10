package model

import java.sql.Date

import play.api.libs.functional.syntax._
import play.api.libs.json.{Format, JsPath}
import util.Util.DateJsonParser

/**
  * Created by chlr on 7/2/17.
  */

case class InstanceQuery(startDate: Date, endDate: Date, status: Option[Int], groupId: String)

object InstanceQuery {

  implicit val formatter: Format[InstanceQuery] = (
    (JsPath \ "start_date").format[Date] and
    (JsPath \ "end_date").format[Date] and
    (JsPath \ "status").formatNullable[Int] and
    (JsPath \ "group_id").format[String]
  )(InstanceQuery.apply, unlift(InstanceQuery.unapply))
}
