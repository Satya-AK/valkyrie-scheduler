package repo

import com.google.inject.Inject
import model.AppTrigger
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile
import table.{CronTriggerTable, TriggerTable}
import util.AppException.EntityNotFoundException
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by chlr on 5/29/17.
  */


class TriggerRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider,
                                  protected val triggerTable: TriggerTable,
                                  protected val cronTriggerTable: CronTriggerTable)
  extends HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._

  /**
    * get trigger
    * @param triggerName
    * @param groupName
    * @return
    */
  def getTrigger(triggerName: String, groupName: String) = {
    val action = for {
      mainTrigger <- triggerTable.table.filter(x => x.groupName === groupName && x.triggerName === triggerName)
      cronTrigger <- cronTriggerTable.table.filter(x => x.groupName === groupName && x.triggerName === triggerName)
    } yield mainTrigger -> cronTrigger

    db.run(action.result.headOption) flatMap {
      case Some((x,y)) => Future.successful(AppTrigger.create(x,y))
      case None => Future.failed(new EntityNotFoundException(s"trigger $triggerName not not found in group $groupName"))
    }
  }


  def listTriggers(groupName: String) = {
    val action = for {
      mainTrigger <- triggerTable.table.filter(x => x.groupName === groupName)
      cronTrigger <- cronTriggerTable.table.filter(x => x.groupName === groupName && x.triggerName === mainTrigger.triggerName)
    } yield mainTrigger -> cronTrigger
    db.run(action.result).map(x => x.map(y => AppTrigger.create(y._1, y._2)))
  }


}