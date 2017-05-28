package repo

import com.google.inject.Inject
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile
import table.{CronTriggerTable, JobTable, TriggerTable}
import util.AppException.EntityNotFoundException
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by chlr on 5/27/17.
  */

class DBRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider,
                             protected val jobTable: JobTable,
                             protected val triggerTable: TriggerTable,
                             protected val cronTriggerTable: CronTriggerTable)
  extends HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._

  /**
    * get job
    * @param jobName
    * @param groupName
    * @return
    */
  def getJob(jobName: String, groupName: String) = {
    db.run(jobTable.table
      .filter(x => x.groupName === groupName && x.jobName === jobName)
      .result.headOption) flatMap {
      case Some(x) => Future.successful(x)
      case None => Future.failed(new EntityNotFoundException(s"job $jobName not not found in group $groupName"))
    }
  }

  /**
    * get trigger
    * @param triggerName
    * @param groupName
    * @return
    */
  def getTrigger(triggerName: String, groupName: String) = {
    db.run(triggerTable.table
      .filter(x => x.triggerName === triggerName && x.groupName === groupName)
      .result.headOption) flatMap {
      case Some(x) => Future.successful(x)
      case None => Future.failed(new EntityNotFoundException(s"trigger $triggerName not not found in group $groupName"))
    }
  }

  /**
    * get cron trigger
    * @param triggerName
    * @param groupName
    * @return
    */
  def getCronTrigger(triggerName: String, groupName: String) = {
    db.run(cronTriggerTable.table
      .filter(x => x.triggerName === triggerName && x.groupName === groupName)
      .result.headOption) flatMap {
      case Some(x) => Future.successful(x)
      case None => Future.failed(new EntityNotFoundException(s"trigger $triggerName not not found in group $groupName"))
    }
  }

}