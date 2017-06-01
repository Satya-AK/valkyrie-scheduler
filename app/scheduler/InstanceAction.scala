package scheduler

import play.api.mvc.Call

/**
  * Created by chlr on 6/1/17.
  */


sealed trait InstanceAction {

  /**
    * REST call description
    */
  val requestCall: Call

  /**
    * target agent identifier
    */
  val accessPoint: String




  
}

object InstanceAction {

  /**
    * action to kill an instance
    * @param instanceId
    * @param accessPoint
    */
  case class KillAction(instanceId: String, accessPoint: String) extends InstanceAction {

    val requestCall = controllers.routes.InstanceController.requestInstanceKill(instanceId)

  }

  /**
    * action to fetch the logs an instance
    * @param instanceId
    * @param accessPoint
    */
  case class FetchLogAction(instanceId: String, accessPoint: String) extends InstanceAction {

    val requestCall = controllers.routes.InstanceController.requestShowLog(instanceId)

  }

}
