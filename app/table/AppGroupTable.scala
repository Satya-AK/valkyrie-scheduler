package table

import com.google.inject.Inject
import model.AppGroup
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile

/**
  * Created by chlr on 6/3/17.
  */

class AppGroupTable @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
  extends HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._

  val table: TableQuery[TableDef] = TableQuery[TableDef]
  val schema = table.schema

  class TableDef(tag: Tag) extends Table[AppGroup](tag, "APP_GROUP") {
    def id =  column[String]("ID", O.SqlType("VARCHAR(40)"))
    def groupName = column[String]("GROUP_NAME", O.SqlType("VARCHAR(30)"))
    def groupEmail = column[String]("GROUP_EMAIL", O.SqlType("VARCHAR(300)"))
    def description = column[String]("DESCRIPTION", O.SqlType("VARCHAR(200)"))
    override def * = (id, groupName, groupEmail ,description) <> (AppGroup.tupled, AppGroup.unapply)
  }

}
