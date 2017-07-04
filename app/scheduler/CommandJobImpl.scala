package scheduler

import repo.AppInstanceRepository
import scheduler.DBManager.DBConnection
import util.{GlobalContext, ServiceHelper}
import util.Util._

/**
  * Created by chlr on 5/27/17.
  */
class CommandJobImpl extends CommandJob {

  protected val instanceRepository = GlobalContext.injector.getInstance(classOf[AppInstanceRepository])
  protected val processCache = GlobalContext.injector.getInstance(classOf[ProcessCache])
  val instanceId = uuid
  override protected val serviceHelper: ServiceHelper = GlobalContext.injector.getInstance(classOf[ServiceHelper])
  override protected val dBConnection: DBManager.DBConnection = {
    val config = GlobalContext.application.configuration.getConfig("slick.dbs.default.db").get
    DBConnection(config.getString("driver").get,
      config.getString("url").get,
      config.getString("user").get,
      config.getString("password").get
    )
  }
}
