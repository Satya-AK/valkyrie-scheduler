package util

import com.google.inject.AbstractModule
import scheduler.{ProcessCache, ProcessCacheImpl, Scheduler}

/**
  * Created by chlr on 5/29/17.
  */


class Module extends AbstractModule {
  override def configure() = {
    bind(classOf[GlobalContext]).asEagerSingleton()
    bind(classOf[Scheduler]).asEagerSingleton()
    bind(classOf[ProcessCache]).to(classOf[ProcessCacheImpl])
  }
}
