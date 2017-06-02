package repo

import com.google.inject.Inject
import model.AppInstanceLog
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile
import table.AppInstanceLogTable

/**
  * Created by chlr on 5/31/17.
  */
class AppInstanceLogRepository @Inject() (protected val dbConfigProvider: DatabaseConfigProvider,
                                          appInstanceLogTable: AppInstanceLogTable)
  extends HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._

  /**
    * save instance log in database
    * @param appInstanceLog
    * @return
    */
  def save(appInstanceLog: AppInstanceLog) = {
    println("*" * 60)
    db.run(appInstanceLogTable.table += appInstanceLog)
  }

  /**
    * fetch instance log
    * @param instanceId
    * @param logType
    * @return
    */
  def fetch(instanceId: String, logType: String) = {
    db.run(appInstanceLogTable
      .table.filter(x => x.instanceId === instanceId && x.logType === logType)
      .map(_.logData).result.headOption
    )
  }

}
