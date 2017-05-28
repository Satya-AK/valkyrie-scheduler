package controllers

import com.google.inject.Inject
import play.api.mvc.{Action, Controller}
import repo.DBRepository

import scala.concurrent.Future

/**
  * Created by chlr on 5/27/17.
  */
class JobController @Inject() (dBRepository: DBRepository) extends Controller {


  def createJob = Action.async {
    Future.successful(Ok(""))
  }

}
