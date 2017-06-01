package repo

import com.google.inject.Inject
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile
import table.AppStatusTable
import util.AppException.EntityNotFoundException
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by chlr on 5/29/17.
  */

class AppStatusRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider,
                                    protected val statusTable: AppStatusTable)
  extends HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._

  /**
    *
    * get status by name
    * @param status
    * @return
    */
  def getStatusName(status: String) = {
    db.run(statusTable.table.filter(_.statusName === status).result.headOption) flatMap {
      case Some(x) => Future.successful(x)
      case None => Future.failed(new EntityNotFoundException(s"status $status not found"))
    }
  }

}

object AppStatusRepository {

  object Status {
    val success = "succeeded"
    val fail = "failed"
    val running = "running"
    val finished = "finished"
    val error = "error"
  }

}
