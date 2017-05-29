package util

import com.google.inject.{AbstractModule, Inject, Injector, Singleton}

/**
  * Created by chlr on 5/29/17.
  */


@Singleton
class GlobalContext @Inject()(injector: Injector) {
  GlobalContext.injector = injector
}

object GlobalContext {

   var injector: Injector = null

  class Module extends AbstractModule {
    override def configure() = {
      bind(classOf[GlobalContext]).asEagerSingleton()
    }
  }
}