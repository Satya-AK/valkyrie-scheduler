package util

import java.sql.Timestamp
import java.util.Properties

import play.api.Configuration
import play.api.libs.json._

import scala.concurrent.Future


/**
  * Created by chlr on 5/27/17.
  */
object Util {


  def uuid = java.util.UUID.randomUUID.toString.replace("-", "")

  def hostName = java.net.InetAddress.getLocalHost.getHostName

  def currentTimeStamp = new Timestamp(new java.util.Date().getTime)

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

}
