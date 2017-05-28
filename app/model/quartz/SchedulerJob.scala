package model.quartz

/**
  * Created by chlr on 5/27/17.
  */

/**
  *
  * @param jobName
  * @param jobGroup
  * @param desc
  */
case class SchedulerJob(jobName: String,
                        jobGroup: String,
                        desc: Option[String])

