package util

import play.api.libs.json.{JsArray, Json}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import util.Util._
/**
  * Created by chlr on 6/4/17.
  */

trait GroupHelper {

  self : AppSpec =>

  /**
    *
    * @param groupName
    * @return
    */
  def createGroupJson(groupName: String) = {
    Json parse
     s"""
        |{
        | "id" : "$uuid",
        | "name" : "$groupName",
        | "description": "this is a test"
        |}
      """.stripMargin
  }

  /**
    * create group
    * @param groupName
    * @return
    */
  def createGroup(groupName: String) = {
    val json = createGroupJson(groupName)
    val Some(result) = route(app, FakeRequest(POST,
       controllers.routes.GroupController.createGroup().path())
      .withJsonBody(json))
    status(result) must be (OK)
    (json \ "id").as[String]
  }

  def listGroup = {
    val Some(result) = route(app, FakeRequest(GET, controllers.routes.GroupController.listGroups().path()))
    status(result) must be (OK)
    contentAsJson(result).as[JsArray]
  }

}
