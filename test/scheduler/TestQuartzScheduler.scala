package scheduler

import com.google.inject.Inject
import play.api.Application
import util.GlobalContext

/**
  * Created by chlr on 6/4/17.
  */
class TestQuartzScheduler @Inject() (application: Application,
                                    globalContext: GlobalContext)
  extends QuartzScheduler(application, globalContext) {

  override def setup() = ()

}
