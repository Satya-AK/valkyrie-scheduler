package model

import play.api.libs.json.{JsPath, Reads, Writes}
import play.api.libs.functional.syntax._

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


  implicit val jsonWriter: Writes[AppGroup] = (
    (JsPath \ "id").write[String] and
    (JsPath \ "name").write[String] and
    (JsPath \ "description").write[String]
  ) (unlift(AppGroup.unapply))

  implicit val jsonReader: Reads[AppGroup] = (
    (JsPath \ "id").read[String] and
    (JsPath \ "name").read[String] and
    (JsPath \ "description").read[String]
  ) (AppGroup.apply _)

}