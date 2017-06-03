package controllers

import com.google.inject.Inject
import model.AppGroup
import model.AppGroup.jsonReader
import play.api.libs.json.{JsArray, Json}
import play.api.mvc.Controller
import repo.AppGroupRepository
import util.{ErrRecoveryAction, Util}

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by chlr on 6/3/17.
  */

class GroupController @Inject() (groupRepository: AppGroupRepository)
  extends Controller {

  /**
    * create group
    * @param groupName
    * @return
    */
  def createGroup(groupName: String) = ErrRecoveryAction.async(parse.json) {
    request =>
      for {
        appGroup <- Util.parseJson[AppGroup](request.body)
        _ <- groupRepository.createGroup(appGroup)
      } yield {
        Ok(Json.obj("success" -> "group create"))
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
