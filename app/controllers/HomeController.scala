package controllers

import javax.inject._
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.libs.json.JsArray
import play.api.mvc._
import repo.SchedulerStateRepository
import table.AppSchemaManager
import util.ErrRecoveryAction

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject() (appSchemaManager: AppSchemaManager,
                                schedulerStateRepository: SchedulerStateRepository) extends Controller {

  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index = Action { implicit request =>
    Ok(views.html.index())
  }


  def evolutions = Action {
    Ok {
      appSchemaManager.evolution
    }
  }

  /**
    * get agents
    * @return
    */
  def agents = ErrRecoveryAction.async {
    schedulerStateRepository.getAgent
      .map(x => Ok(x.map(_.json).foldLeft(JsArray())({ case (arr,node) => arr :+ node })))
  }

}
