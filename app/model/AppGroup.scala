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
case class AppGroup(id: Option[Int], groupName: String, description: String)

object AppGroup {

  def tupled = (AppGroup.apply _).tupled

  def unravel(arg: AppGroup): Option[(Option[Int], String, String)] = {
    Some((arg.id, arg.groupName, arg.description))
  }

  def create(groupName: String, description: String) = new AppGroup(None, groupName, description)

  implicit val jsonWriter: Writes[AppGroup] = (
    (JsPath \ "id").writeNullable[Int] and
    (JsPath \ "name").write[String] and
    (JsPath \ "description").write[String]
  ) (unlift(AppGroup.unravel))

  implicit val jsonReader: Reads[AppGroup] = (
    (JsPath \ "name").read[String] and
    (JsPath \ "description").read[String]
  ) (AppGroup.create _)

}