package util

import com.google.inject.AbstractModule
import scheduler.Scheduler

/**
  * Created by chlr on 5/29/17.
  */


class Module extends AbstractModule {
  override def configure() = {
    bind(classOf[GlobalContext]).asEagerSingleton()
    bind(classOf[Scheduler]).asEagerSingleton()
  }
}
