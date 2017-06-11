package model

import java.sql.Timestamp

/**
  * Created by chlr on 5/28/17.
  */

case class AppInstance(id: String,
                       groupName: String,
                       jobName: String,
                       triggerName: Option[String],
                       startTime: Timestamp,
                       endTime: Option[Timestamp],
                       message: Option[String],
                       returnCode: Option[Int],
                       seqId: Long,
                       statusId: Int,
                       attempt: Int,
                       agent: String)
