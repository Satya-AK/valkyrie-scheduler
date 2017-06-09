package scheduler

import util.{AppSpec, Keyword}

/**
  * Created by chlr on 6/5/17.
  */
class CommandExecutorSpec extends AppSpec {


  "CommandExecutor" must {
    "executor job" in {
      val map = Map[String, String]()
      map.map(_ => 10)
      val commandJob = new CommandJob()
      val jobName = "job_command_executor_test"
      val groupId = "group_command_executor_group_id_test"
      val triggerName = "trigger_command_executor_test"
      commandJob.execute(new TestJobExecutionContext(jobName,
        groupId,
        triggerName,
        Map(Keyword.JobData.command -> "echo hello world"))
      )
    }
  }
}
