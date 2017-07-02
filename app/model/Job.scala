package model

import java.sql.Blob

/**
  * Created by chlr on 5/27/17.
  */

/**
  * @param jobId
  * @param groupId
  * @param desc
  */
case class Job(jobId: String,
               groupId: String,
               desc: Option[String],
               jobData: Option[Blob]) {

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


