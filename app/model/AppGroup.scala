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
case class AppGroup(id: String, groupName: String, description: String)

object AppGroup {

  def tupled = (AppGroup.apply _).tupled

  implicit val formatter: Format[AppGroup] = (
    (JsPath \ "id").format[String] and
    (JsPath \ "name").format[String] and
    (JsPath \ "description").format[String]
  )(AppGroup.apply, unlift(AppGroup.unapply))

}