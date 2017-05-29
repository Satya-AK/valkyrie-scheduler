package scheduler

import org.quartz.{Job, JobExecutionContext}
import repo.InstanceRepository
import repo.StatusRepository.Status
import util.GlobalContext
import util.Util._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

/**
  * Created by chlr on 5/27/17.
  */
class CommandJob extends Job {

  val instanceRepository = GlobalContext.injector.getInstance(classOf[InstanceRepository])

  override def execute(context: JobExecutionContext): Unit = {
    val instanceId = uuid
    instanceRepository.createInstance(
      instanceId
      ,context.getJobDetail.getKey.getGroup
      ,context.getTrigger.getKey.getName
      ,Some(context.getJobDetail.getKey.getName)) flatMap {
      _ => Future {
          val executor = new CommandExecutor(context.getJobDetail.getJobDataMap.getString("command"))
          executor.execute()
        }
    } onComplete  {
      case Success(_) => instanceRepository.endInstance(instanceId, Status.success)
      case Failure(_) => instanceRepository.endInstance(instanceId, Status.fail)
    }
  }

}
