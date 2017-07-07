package table

import com.google.inject.Inject
import model.SchedulerState
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile

/**
  * Created by chlr on 7/6/17.
  */

class SchedulerStateTable @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
  extends HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._

  val table: TableQuery[TableDef] = TableQuery[TableDef]
  val schema = table.schema

  class TableDef(tag: Tag) extends Table[SchedulerState](tag, "QRTZ_SCHEDULER_STATE") {
    def schedulerName =  column[String]("SCHED_NAME", O.SqlType("VARCHAR(40)"))
    def instanceName = column[String]("INSTANCE_NAME", O.SqlType("VARCHAR(30)"))
    def lastCheckIn = column[Long]("LAST_CHECKIN_TIME", O.SqlType("VARCHAR(300)"))
    def checkInInterval = column[Long]("CHECKIN_INTERVAL", O.SqlType("VARCHAR(200)"))
    override def * = (schedulerName, instanceName, lastCheckIn ,checkInInterval) <> (SchedulerState.tupled,
      SchedulerState.unapply)
  }

}