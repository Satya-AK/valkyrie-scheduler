package controllers

import com.google.inject.Inject
import model.AppGroup
import model.AppGroup.formatter
import play.api.libs.json.{JsArray, Json}
import play.api.mvc.Controller
import repo.AppGroupRepository
import util.Util._
import util.{ErrRecoveryAction, Util}

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
        appGroup <- Util.parseJson[AppGroup](request.body)
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

  /**
    * fetch group info
    * @param groupId
    * @return
    */
  def fetchGroup(groupId: String) = ErrRecoveryAction.async {
    groupRepository
      .getGroupById(groupId)
      .map(x => Ok(Json.toJson(x)))
  }

  /**
    *
    * @return
    */
  def update = ErrRecoveryAction.async(parse.json) {
    request =>
      for {
        group <- parseJson[AppGroup](request.body)
        _ <- groupRepository.update(group)
      } yield {
        Ok(Json.obj("message" -> s"group ${group.groupName} updated"))
      }
  }

}
