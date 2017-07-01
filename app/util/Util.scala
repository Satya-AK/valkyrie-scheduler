package util

import java.io.File
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Properties

import play.api.Configuration
import play.api.libs.json._
import _root_.util.AppException.AppSetupException
import _root_.util.Keyword.AppSetting

import scala.concurrent.Future
import scala.util.{Failure, Success, Try}


/**
  * Created by chlr on 5/27/17.
  */
object Util {

  /**
    * get hostname
    * @return
    */
  def hostName = {
    GlobalContext.application.configuration.getString(s"app.${AppSetting.hostName}") match {
      case None => java.net.InetAddress.getLocalHost.getHostName
      case Some(x) => x
    }
  }

  /**
    * uuid generator
    * @return
    */
  def uuid = java.util.UUID.randomUUID.toString.replace("-", "")

  /**
    * current timestamp
    * @return
    */
  def currentTimeStamp = new Timestamp(new java.util.Date().getTime)

  /**
    * join paths
    * @param paths
    * @return
    */
  def joinPath(paths: String *): String = {
    paths.foldLeft(System.getProperty("file.separator"))((acc: String, path: String) => new File(acc, path).toString)
  }

  /**
    * play configuration to java properties object.
    * @param config
    * @param configuration
    * @return
    */
  def configToProperties(config: play.api.Configuration, configuration: Configuration) = {
    val properties = new Properties()
    val mapper = Map(
      "slick.dbs.default.db.driver" -> "org.quartz.dataSource.quartzDataSource.driver",
      "slick.dbs.default.db.url" -> "org.quartz.dataSource.quartzDataSource.URL",
      "slick.dbs.default.db.user" -> "org.quartz.dataSource.quartzDataSource.user",
      "slick.dbs.default.db.password" -> "org.quartz.dataSource.quartzDataSource.password"
    )
    config.keys.foreach(x => properties.setProperty(x, config.getString(x).get))
    properties.setProperty("org.quartz.scheduler.instanceName", hostName)
    for ((slickProperty,quartzProperty) <- mapper.toList ) {
      properties.setProperty(quartzProperty, configuration.getString(slickProperty).get)
    }
    properties
  }


  /**
    * parse json to Future[T]
    * @param json
    * @tparam T
    * @return
    */
  def parseJson[T: Reads](json: JsValue) = {
    Json.fromJson[T](json) match {
      case JsSuccess(x, _) => Future.successful(x)
      case JsError(th) => Future.failed(new AppException.ParseException(""))
    }
  }

  /**
    *
    * @return
    */
  def appDirectory = {
    GlobalContext.application.configuration.getString(s"app.${AppSetting.tmpDir}") match {
      case None => throw new AppSetupException(s"key app.${AppSetting.tmpDir} not defined in application conf")
      case Some(x) => x
    }
  }


  /**
    *
    * @param instanceId
    * @return
    */
  def instanceLogDirectory(instanceId: String) = joinPath(appDirectory, "logs", instanceId)


  /**
    * JsObject implicit adapter class
    * @param jsObject
    */
  implicit class JsObjectEnhancer(jsObject: JsObject) {

    /**
      * update JsObject
      * @param args
      * @return
      */
    def update(args: (String, Json.JsValueWrapper)*): JsObject = {
      jsObject ++ Json.obj(args: _*)
    }

    def remove(fields: String *): JsObject = {
      fields.foldLeft(jsObject)((acc,x) => acc - x)
    }

  }

  implicit object TimestampJsonParser extends Reads[Timestamp] with Writes[Timestamp] {

    private val sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

    override def reads(json: JsValue): JsResult[Timestamp] = {
      Try(new Timestamp(sdf.parse(json.as[String]).getTime)) match {
        case Success(x) => JsSuccess(x)
        case Failure(th) => JsError(th.getMessage)
      }
    }
    override def writes(o: Timestamp): JsValue = {
      JsString(sdf.format(o))
    }
    def format(time: Timestamp) = sdf.format(time)
    def parse(time: String) = new Timestamp(sdf.parse(time).getTime)

  }

}
