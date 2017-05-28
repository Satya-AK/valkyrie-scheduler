package table

import com.google.inject.Inject
import model.quartz.SchedulerJob
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile

/**
  * Created by chlr on 5/27/17.
  */


class JobTable @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
  extends HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._

  val table: TableQuery[TableDef] = TableQuery[TableDef]

  class TableDef(tag: Tag) extends Table[SchedulerJob](tag, "QRTZ_JOB_DETAILS") {
    def jobName =  column[String]("JOB_NAME")
    def groupName = column[String]("JOB_GROUP")
    def description = column[Option[String]]("DESCRIPTION")
    override def * = (jobName, groupName, description) <> (SchedulerJob.tupled, SchedulerJob.unapply)
  }

}

