package util

import com.google.inject.{Inject, Injector, Singleton}
import play.api.Application
import util.AppException.AppSetupException
import util.Keyword.AppSetting
import util.Util.joinPath

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

  /**
    *
    * @return
    */
  def tmpDirectory = {
    application.configuration.getString(s"app.${AppSetting.tmpDir}") match {
      case None => throw new AppSetupException(s"key app.${AppSetting.tmpDir} not defined in application conf")
      case Some(x) => x
    }
   }


  /**
    *
    * @param instanceId
    * @return
    */
   def instanceLogDirectory(instanceId: String) = joinPath(tmpDirectory, "logs", instanceId)


}