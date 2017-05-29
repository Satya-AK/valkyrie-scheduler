package scheduler

import org.quartz.{Job, JobExecutionContext}

/**
  * Created by chlr on 5/27/17.
  */
class CommandJob extends Job {

  override def execute(context: JobExecutionContext): Unit = {
      context.getJobDetail.getJobDataMap
  }

}
