package util

import com.google.inject.AbstractModule
import repo.AppInstanceRepository
import repo.impl.AppInstanceRepositoryImpl
import scheduler.{ProcessCache, ProcessCacheImpl, Scheduler, TestQuartzScheduler}

/**
  * Created by chlr on 6/4/17.
  */

class TestModule extends AbstractModule {

  override def configure(): Unit = {
    bind(classOf[GlobalContext]).asEagerSingleton()
    bind(classOf[ProcessCache]).to(classOf[ProcessCacheImpl])
    bind(classOf[Scheduler]).to(classOf[TestQuartzScheduler]).asEagerSingleton()
    bind(classOf[AppInstanceRepository]).to(classOf[AppInstanceRepositoryImpl])
  }

}
