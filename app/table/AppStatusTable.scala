package table

import com.google.inject.Inject
import model.AppStatus
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile

/**
  * Created by chlr on 5/29/17.
  */


class AppStatusTable @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
  extends HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._

  val table: TableQuery[TableDef] = TableQuery[TableDef]

  val schema = table.schema

  class TableDef(tag: Tag) extends Table[AppStatus](tag, "APP_STATUS") {
    def id =  column[Int]("ID")
    def statusName = column[String]("STATUS", O.SqlType("varchar(40)"))
    override def * = (id, statusName) <> (AppStatus.tupled, AppStatus.unapply)
  }
}
