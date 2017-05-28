package table

import com.google.inject.Inject
import model.quartz.SchedulerCronTrigger
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile

/**
  * Created by chlr on 5/27/17.
  */


class CronTriggerTable @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
  extends HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._

  val table: TableQuery[TableDef] = TableQuery[TableDef]

  class TableDef(tag: Tag) extends Table[SchedulerCronTrigger](tag, "QRTZ_JOB_DETAILS") {
    def triggerName =  column[String]("TRIGGER_NAME")
    def groupName = column[String]("TRIGGER_GROUP")
    def cronExpression =  column[String]("CRON_EXPRESSION")
    override def * = (triggerName, groupName, cronExpression) <> (SchedulerCronTrigger.tupled, SchedulerCronTrigger.unapply)
  }

}
