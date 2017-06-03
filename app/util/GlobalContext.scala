package util

import com.google.inject.{Inject, Injector, Singleton}
import play.api.Application

/**
  * Created by chlr on 5/29/17.
  */


@Singleton
class GlobalContext @Inject()(injector: Injector
                             ,application: Application) {

  GlobalContext._injector = injector
  GlobalContext._application = application

}

object GlobalContext {

   private[GlobalContext] var _injector: Injector = null

   private[GlobalContext] var _application: Application = null

   def injector = _injector

   def application = _application

}