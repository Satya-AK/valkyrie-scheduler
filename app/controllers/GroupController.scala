package controllers

import com.google.inject.Inject
import model.AppGroup
import model.AppGroup.jsonReader
import play.api.libs.json.{JsArray, JsObject, Json}
import play.api.mvc.Controller
import repo.AppGroupRepository
import util.{ErrRecoveryAction, Util}
import util.Util._

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by chlr on 6/3/17.
  */

class GroupController @Inject() (groupRepository: AppGroupRepository)
  extends Controller {

  /**
    * create group
    * @return
    */
  def createGroup = ErrRecoveryAction.async(parse.json) {
    request =>
      val groupId = uuid
      for {
        appGroup <- Util.parseJson[AppGroup](request.body.as[JsObject].update("id" -> groupId))
        _ <- groupRepository.createGroup(appGroup)
      } yield {
        Ok(Json.obj("id" -> s"$groupId"))
      }
  }

  /**
    * list groups
    * @return
    */
  def listGroups = ErrRecoveryAction.async {
      groupRepository.listGroups
        .map(x => x.map(Json.toJson(_)).foldLeft(JsArray())({ case (acc, node) => acc :+ node}))
        .map(Ok(_))
  }

}
