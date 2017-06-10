package scheduler

import util.Util._
import util.{AppSpec, Keyword}
/**
  * Created by chlr on 6/5/17.
  */
class CommandExecutorSpec extends AppSpec {


  "CommandExecutor" must {
    "executor job" in {
      val testAppInstanceRepository =  instanceRepository
      val testInstanceId = uuid
      val commandJob = new CommandJob {
        override val instanceId = testInstanceId
        override val instanceRepository = testAppInstanceRepository
        override val processCache = new ProcessCacheImpl()
      }
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
