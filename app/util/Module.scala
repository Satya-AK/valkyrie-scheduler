package util

import com.google.inject.AbstractModule
import net.codingwell.scalaguice.ScalaModule
import repo.AppInstanceRepository
import repo.impl.AppInstanceRepositoryImpl
import scheduler.{ProcessCache, ProcessCacheImpl, QuartzScheduler, Scheduler}

/**
  * Created by chlr on 5/29/17.
  */


class Module extends AbstractModule with ScalaModule {
  override def configure() = {
    bind(classOf[GlobalContext]).asEagerSingleton()
    bind(classOf[ProcessCache]).to(classOf[ProcessCacheImpl])
    bind(classOf[Scheduler]).to(classOf[QuartzScheduler]).asEagerSingleton()
    bind[AppInstanceRepository].to[AppInstanceRepositoryImpl]
  }
}
