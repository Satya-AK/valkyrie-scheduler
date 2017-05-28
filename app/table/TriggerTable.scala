package table

import com.google.inject.Inject
import model.Trigger
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile

/**
  * Created by chlr on 5/27/17.
  */


class TriggerTable @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
  extends HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._

  val table: TableQuery[TableDef] = TableQuery[TableDef]

  class TableDef(tag: Tag) extends Table[Trigger](tag, "QRTZ_JOB_DETAILS") {
    def triggerName =  column[String]("TRIGGER_NAME")
    def groupName = column[String]("TRIGGER_GROUP")
    def jobName =  column[String]("JOB_NAME")
    def jobGroup = column[String]("JOB_GROUP")
    def description = column[Option[String]]("DESCRIPTION")
    def nextFireTime = column[Option[Long]]("NEXT_FIRE_TIME")
    def previousFireTime = column[Option[Long]]("PREV_FIRE_TIME")
    override def * = (triggerName, groupName, jobName, jobGroup, description, nextFireTime, previousFireTime) <> (Trigger.tupled, Trigger.unapply)
  }

}
