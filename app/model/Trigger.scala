package model

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
                   nextFireTime: Option[Long],
                   previousFireTime: Option[Long])
