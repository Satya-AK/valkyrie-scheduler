package table

import com.google.inject.Inject
import model.AppInstanceLog
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile

/**
  * Created by chlr on 5/31/17.
  */

class AppInstanceLogTable @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
  extends HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._

  val table: TableQuery[TableDef] = TableQuery[TableDef]
  val schema = table.schema

  class TableDef(tag: Tag) extends Table[AppInstanceLog](tag, "APP_INSTANCE_LOG") {
    def instanceId =  column[String]("INSTANCE_ID", O.SqlType("varchar(40)"))
    def logType = column[String]("LOG_TYPE", O.SqlType("varchar(10)"))
    def logData = column[Option[String]]("LOG_DATA", O.SqlType("TEXT"))
    override def * = (instanceId, logType, logData) <> (AppInstanceLog.tupled, AppInstanceLog.unapply)
  }
}
