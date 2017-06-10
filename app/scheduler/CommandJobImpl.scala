package scheduler

import repo.AppInstanceRepository
import util.GlobalContext
import util.Util._

/**
  * Created by chlr on 5/27/17.
  */
class CommandJobImpl extends CommandJob {

  protected val instanceRepository = GlobalContext.injector.getInstance(classOf[AppInstanceRepository])
  protected val processCache = GlobalContext.injector.getInstance(classOf[ProcessCache])
  val instanceId = uuid

}
