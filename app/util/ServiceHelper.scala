package util

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.Application
import play.api.libs.json.JsValue
import play.api.libs.ws.WSClient
import play.api.mvc.Results
import scheduler.InstanceAction
import util.Util.hostName

/**
  * Created by chlr on 6/1/17.
  */

@Singleton
class ServiceHelper @Inject() (wSClient: WSClient
                              ,application: Application) {


  /**
    * make remote REST call to another agent to perform an action
    * @param action
    * @param body
    * @param statusCode
    * @return
    */
  def requestAction(action: InstanceAction, body: Option[JsValue], statusCode: Int = 200) = {
    val url = s"http://${action.accessPoint}${action.requestCall.path}"
    println(url)
    val response = action.requestCall.method match {
      case "POST" => body match {
        case Some(x) => wSClient.url(url).post(x)
        case None => wSClient.url(url).post(Results.EmptyContent())
      }
      case "GET" => wSClient.url(url).get()
    }
    response.filter(x => x.status == statusCode).map(_.json)
  }


  /**
    * get access point for this instance
    * @return
    */
  def accessPoint = {
    s"$hostName:${application.configuration.getString("http.port").getOrElse(9000)}"
  }


}
