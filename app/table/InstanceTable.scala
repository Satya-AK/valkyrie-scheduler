package table

import java.sql.Timestamp

import com.google.inject.Inject
import model.Instance
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile

/**
  * Created by chlr on 5/28/17.
  */

class InstanceTable @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
  extends HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._

  val table: TableQuery[TableDef] = TableQuery[TableDef]

  val schema = table.schema

  class TableDef(tag: Tag) extends Table[Instance](tag, "APP_INSTANCE") {
    def instanceId =  column[String]("INSTANCE_ID", O.SqlType("varchar(40)"))
    def groupName = column[String]("GROUP_NAME", O.SqlType("varchar(100)"))
    def jobName = column[String]("JOB_NAME", O.SqlType("varchar(100)"))
    def triggerName = column[Option[String]]("TRIGGER_NAME", O.SqlType("varchar(100)"))
    def startTime = column[Timestamp]("START_TIME")
    def endTime = column[Option[Timestamp]]("END_TIME")
    def seqId = column[Long]("SEQ_ID")
    def statusId = column[Int]("STATUS_ID")
    def attempt = column[Int]("attempt")
    def agentName = column[String]("AGENT_NAME", O.SqlType("varchar(100)"))

    override def * = (instanceId, groupName, jobName, triggerName, startTime, endTime,
      seqId, statusId, attempt, agentName) <> (Instance.tupled, Instance.unapply)
  }

}