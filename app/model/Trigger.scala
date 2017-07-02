package model

import java.sql.Blob

/**
  * Created by chlr on 5/27/17.
  */

/**
  *
  * @param triggerId
  * @param groupId
  * @param jobName
  * @param jobId
  * @param desc
  * @param state
  * @param nextFireTime
  * @param previousFireTime
  */
case class Trigger(triggerId: String,
                   groupId: String,
                   jobName: String,
                   jobId: String,
                   desc: Option[String],
                   state: String,
                   jobData: Option[Blob],
                   nextFireTime: Option[Long],
                   previousFireTime: Option[Long]) {

  def data = jobData
    .map(x => new String(x.getBytes(1, x.length().asInstanceOf[Int])))
    .map(x => x.split(System.lineSeparator)
      .filterNot(_.startsWith("#"))
      .map(x => x.split("=").toList match {
        case x :: tail => x -> tail.mkString("=")
        case Nil => "" -> ""
      })
      .toMap)
}
