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
    def id =  column[Option[Int]]("ID", O.PrimaryKey ,O.AutoInc)
    def groupName = column[String]("GROUP_NAME", O.SqlType("VARCHAR(30)"))
    def description = column[String]("DESCRIPTION", O.SqlType("VARCHAR(200)"))
    override def * = (id, groupName, description) <> (AppGroup.tupled, AppGroup.unapply)
  }

}
