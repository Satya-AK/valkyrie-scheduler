package util

/**
  * Created by chlr on 6/2/17.
  */

import play.api.Logger
import play.api.libs.json.Json
import play.api.mvc.Results.Status
import play.api.mvc.{ActionBuilder, Request, Result}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by chlr on 12/2/16.
  */


object ErrRecoveryAction extends ActionBuilder[Request] {

  private val logger = Logger(this.getClass)

  type HTTPStatusCode = Int

  private val INTERNAL_SERVER_ERROR: HTTPStatusCode = 500

  private val BAD_REQUEST: HTTPStatusCode = 400

  def invokeBlock[A](request: Request[A], block: Request[A] => Future[Result]) = {
    block(request) recover (errorLogger andThen errorHandler)
  }

  /**
    * generate play response with Exception
    * @return
    */
  def errorHandler: PartialFunction[Throwable, Result] = {
    case th: IllegalArgumentException => makeJson(BAD_REQUEST, th)
    case th: java.lang.AssertionError => makeJson(BAD_REQUEST, th)
    case th: AppException => makeJson(BAD_REQUEST, th)
    case th: Throwable => makeJson(INTERNAL_SERVER_ERROR, th)
  }

  /**
    * partial function to log error
    * @return
    */
  def errorLogger: PartialFunction[Throwable, Throwable] = {
    case th: Throwable =>
      logger.error("API Error", th)
      th
  }


  /**
    * helper function to generate play response from Exception
    * @param status
    * @param th
    * @return
    */
  def makeJson(status: HTTPStatusCode,th: Throwable) = {
    new Status(status).apply(Json.obj("error_message" -> th.getMessage, "error_class" -> th.getClass.getName))
  }

}