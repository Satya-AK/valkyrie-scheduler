package model

import play.api.libs.functional.syntax._
import play.api.libs.json._



/**
  * Created by chlr on 5/29/17.
  */

/**
  *
  * @param id
  * @param status
  */
case class AppStatus(id: Int, status: String)

object AppStatus {

  def tupled = (AppStatus.apply _).tupled

  implicit val formatter: Format[AppStatus] = (
    (__ \ "id").format[Int] and
    (__ \ "name").format[String]
  )(AppStatus.apply, unlift(AppStatus.unapply))
}
