package scheduler

import play.api.test.Helpers._
import util.Util._
import util.{AppSpec, Keyword}
/**
  * Created by chlr on 6/5/17.
  */
class CommandExecutorSpec extends AppSpec {

  val testAppInstanceRepository = instanceRepository
  val testInstanceId = uuid
  val commandJob = new CommandJob {
    override val instanceId = testInstanceId
    override val instanceRepository = testAppInstanceRepository
    override val processCache = new ProcessCacheImpl()
  }
  val jobName = "job_command_executor_test"
  val groupName = "group_command_executor_group_id_test"
  val triggerName = "trigger_command_executor_test"

  "CommandExecutor" must {
    "execute job that completes successfuly" in {
      commandJob.execute(new TestJobExecutionContext(jobName,
        groupName,
        triggerName,
        Map(Keyword.JobData.command -> "echo hello world"))
      )
      val instance = await(instanceRepository.fetchInstance(testInstanceId))
      instance.statusId must be (2)
      instance.jobName must be (jobName)
      instance.groupName must be (groupName)
      instance.triggerName.get must be (triggerName)
      instance.returnCode must be (Some(0))
      val instanceLog = await(instanceLogRepository.fetch(testInstanceId, "stdout"))
      instanceLog.get must be ("hello world\n")
    }
  }


  it must {
    "handle jobs that fails execution" in  {
      commandJob.execute(new TestJobExecutionContext(jobName,
        groupName,
        triggerName,
        Map(Keyword.JobData.command -> "echo1 hello world"))
      )
      val instance = await(instanceRepository.fetchInstance(testInstanceId))
      instance.statusId must be (3)
      instance.jobName must be (jobName)
      instance.groupName must be (groupName)
      instance.triggerName.get must be (triggerName)
      instance.returnCode must be (Some(-1))
      instance.message must be (Some("Cannot run program \"echo1\" (in directory \".\"): error=2, No such file or directory"))
    }
  }

}
