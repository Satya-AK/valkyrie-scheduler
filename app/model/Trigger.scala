package model

import java.sql.Blob

/**
  * Created by chlr on 5/27/17.
  */

/**
  *
  * @param triggerName
  * @param groupName
  * @param jobName
  * @param jobGroup
  * @param desc
  * @param nextFireTime
  * @param previousFireTime
  */
case class Trigger(triggerName: String,
                   groupName: String,
                   jobName: String,
                   jobGroup: String,
                   desc: Option[String],
                   jobData: Option[Blob],
                   nextFireTime: Option[Long],
                   previousFireTime: Option[Long]) {

  def data = jobData
    .map(x => new String(x.getBytes(0, x.length().asInstanceOf[Int])))
    .map(x => x.split(System.lineSeparator)
      .filterNot(_.startsWith("#"))
      .map(x => x.split("=").toList match {
        case x :: tail => x -> tail.mkString("=")
        case Nil => "" -> ""
      })
      .toMap)
}
