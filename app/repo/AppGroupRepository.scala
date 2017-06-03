package repo

import com.google.inject.Inject
import model.AppGroup
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile
import table.AppGroupTable
import util.AppException.EntityNotFoundException
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by chlr on 6/3/17.
  */

class AppGroupRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider,
                                   protected val appGroupTable: AppGroupTable)
  extends HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._

  /**
    * create group
    * @param appGroup
    * @return
    */
  def createGroup(appGroup: AppGroup) = {
    db.run(appGroupTable.table += appGroup)
  }


  /**
    * get group by Name
    * @param groupName
    * @return
    */
  def getGroupByName(groupName: String) = {
    db.run(appGroupTable.table.filter(_.groupName === groupName).result.headOption) flatMap {
      case Some(x) => Future.successful(x)
      case None => Future.failed(new EntityNotFoundException(s"group $groupName not found"))
    }
  }


  def listGroups = {
    db.run(appGroupTable.table.result)
  }


}
