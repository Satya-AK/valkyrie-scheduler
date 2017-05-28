package table

import com.google.inject.Inject
import model.CronTrigger
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile

/**
  * Created by chlr on 5/27/17.
  */


class CronTriggerTable @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
  extends HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._

  val table: TableQuery[TableDef] = TableQuery[TableDef]

  class TableDef(tag: Tag) extends Table[CronTrigger](tag, "QRTZ_CRON_TRIGGERS") {
    def triggerName =  column[String]("TRIGGER_NAME")
    def groupName = column[String]("TRIGGER_GROUP")
    def cronExpression =  column[String]("CRON_EXPRESSION")
    override def * = (triggerName, groupName, cronExpression) <> (CronTrigger.tupled, CronTrigger.unapply)
  }

}
