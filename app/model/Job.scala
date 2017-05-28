package model

import java.sql.Blob

/**
  * Created by chlr on 5/27/17.
  */

/**
  *
  * @param jobName
  * @param jobGroup
  * @param desc
  */
case class Job(jobName: String,
               jobGroup: String,
               desc: Option[String],
               jobData: Option[Blob]) {

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


