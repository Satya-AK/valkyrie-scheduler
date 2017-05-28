package model.app

/**
  * Created by chlr on 5/27/17.
  */

/**
  *
  * @param jobName
  * @param groupName
  * @param cmd
  * @param desc
  */
case class Job(jobName: String,
               groupName: String,
               cmd: String,
               desc: Option[String])
