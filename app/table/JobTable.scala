package table

import java.sql.Blob

import com.google.inject.Inject
import model.Job
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile

/**
  * Created by chlr on 5/27/17.
  */


class JobTable @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
  extends HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._

  val table: TableQuery[TableDef] = TableQuery[TableDef]

  class TableDef(tag: Tag) extends Table[Job](tag, "QRTZ_JOB_DETAILS") {
    def jobName =  column[String]("JOB_NAME")
    def groupName = column[String]("JOB_GROUP")
    def description = column[Option[String]]("DESCRIPTION")
    def jobData = column[Option[Blob]]("JOB_DATA")
    override def * = (jobName, groupName, description, jobData) <> (Job.tupled, Job.unapply)
  }

}

