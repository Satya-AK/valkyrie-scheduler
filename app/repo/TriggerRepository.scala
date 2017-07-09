package repo

import com.google.inject.Inject
import model.AppTrigger
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile
import table.{CronTriggerTable, TriggerTable}
import util.AppException.{EntityNotFoundException, InvalidStateException}

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
    * @param triggerId
    * @param groupId
    * @return
    */
  def getTrigger(groupId: String, triggerId: String) = {
    val action = for {
      mainTrigger <- triggerTable.table.filter(x => x.tgrGroupId === groupId && x.triggerName === triggerId)
      cronTrigger <- cronTriggerTable.table.filter(x => x.groupName === groupId && x.triggerName === triggerId)
    } yield mainTrigger -> cronTrigger

    db.run(action.result.headOption) flatMap {
      case Some((x,y)) => Future.successful(AppTrigger.create(x,y))
      case None => Future.failed(new EntityNotFoundException(s"trigger $triggerId not not found in group $groupId"))
    }
  }

  /**
    * check if trigger exists with a name in group while create
    * @param groupId
    * @param triggerName
    * @return
    */
  def checkTriggerNameForCreate(groupId: String, triggerName: String): Future[Unit] = {
    listTriggers(groupId).map(x => x.find(_.triggerName == triggerName)) flatMap {
      case Some(_) => Future.failed(new InvalidStateException(s"trigger with name $triggerName already exists"))
      case None => Future.successful(())
    }
  }

  /**
    *
    * @param groupId
    * @param triggerId
    * @param triggerName
    * @return
    */
  def checkTriggerNameForUpdate(groupId: String, triggerId: String, triggerName: String): Future[Unit] = {
    listTriggers(groupId).map(x => x.filter(_.triggerName == triggerName).toList) flatMap {
      case x :: Nil if x.id == triggerId => Future.successful(())
      case Nil => Future.successful(())
      case x =>
        println(x)
        Future.failed(new InvalidStateException(s"trigger with name $triggerName already exists"))
    }
  }

  /**
    * list trigger
    * @param groupId
    * @return
    */
  def listTriggers(groupId: String): Future[Seq[AppTrigger]] = {
    val action = for {
      mainTrigger <- triggerTable.table.filter(x => x.tgrGroupId === groupId)
      cronTrigger <- cronTriggerTable.table.filter(x => x.groupName === groupId && x.triggerName === mainTrigger.triggerName)
    } yield mainTrigger -> cronTrigger
    db.run(action.result).map(x => x.map(y => AppTrigger.create(y._1, y._2)))
  }


}