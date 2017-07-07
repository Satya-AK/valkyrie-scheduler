package repo

import com.google.inject.Inject
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile
import table.SchedulerStateTable

/**
  * Created by chlr on 7/6/17.
  */
class SchedulerStateRepository @Inject() (protected val dbConfigProvider: DatabaseConfigProvider,
                                          protected val schedulerStateTable: SchedulerStateTable)
  extends HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._

  def getAgent = db.run(schedulerStateTable.table.result)
}
