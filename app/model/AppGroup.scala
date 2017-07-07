package model

import play.api.libs.functional.syntax._
import play.api.libs.json.{Format, JsPath}

/**
  * Created by chlr on 6/3/17.
  */

/**
  *
  * @param id
  * @param groupName
  * @param description
  */
case class AppGroup(id: String, groupName: String, groupEmail: String, description: Option[String])

object AppGroup {

  def tupled = (AppGroup.apply _).tupled

  implicit val formatter: Format[AppGroup] = (
    (JsPath \ "id").format[String] and
    (JsPath \ "name").format[String] and
    (JsPath \ "email").format[String] and
    (JsPath \ "desc").formatNullable[String]
  )(AppGroup.apply, unlift(AppGroup.unapply))

}