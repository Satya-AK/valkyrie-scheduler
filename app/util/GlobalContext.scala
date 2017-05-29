package util

import com.google.inject.{Inject, Injector, Singleton}

/**
  * Created by chlr on 5/29/17.
  */


@Singleton
class GlobalContext @Inject()(injector: Injector) {
  GlobalContext.injector = injector
}

object GlobalContext {
   var injector: Injector = null
}