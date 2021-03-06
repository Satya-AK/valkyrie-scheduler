package table

import java.io.StringWriter

import com.google.inject.Inject

/**
  * Created by chlr on 5/29/17.
  */

class AppSchemaManager @Inject() (instanceTable: AppInstanceTable,
                                  statusTable: AppStatusTable,
                                  instanceLogTable: AppInstanceLogTable,
                                  groupTable: AppGroupTable,
                                  schedulerStateTable: SchedulerStateTable) {

  val schemas = instanceTable.schema :: statusTable.schema :: instanceLogTable.schema :: groupTable.schema :: Nil

  def evolution = {
    val writer = new StringWriter()
    writer.write("# --- !Ups\n\n")
    for (schema <- schemas) {
      schema.createStatements.foreach(s => writer.write(s + ";\n"))
    }
    writer.write("\n\n# --- !Downs\n\n")
    for (schema <- schemas) {
      schema.dropStatements.foreach(s => writer.write(s + ";\n"))
    }
    writer.close()
    writer.toString
  }
}
