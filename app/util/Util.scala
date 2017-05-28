package util

import java.util.Properties

import play.api.Configuration

/**
  * Created by chlr on 5/27/17.
  */
object Util {

  def hostName = java.net.InetAddress.getLocalHost.getHostName


  def configToProperties(config: play.api.Configuration, configuration: Configuration) = {
    val properties = new Properties()
    config.keys.foreach(x => properties.setProperty(x, config.getString(x).get))
    properties.setProperty("org.quartz.scheduler.instanceName", hostName)
    properties.setProperty("org.quartz.dataSource.quartzDataSource.driver",
      configuration.getString("slick.dbs.default.db.driver").get)
    properties.setProperty("org.quartz.dataSource.quartzDataSource.URL",
      configuration.getString("slick.dbs.default.db.url").get)
    properties.setProperty("org.quartz.dataSource.quartzDataSource.user",
      configuration.getString("slick.dbs.default.db.user").get)
    properties.setProperty("org.quartz.dataSource.quartzDataSource.password",
      configuration.getString("slick.dbs.default.db.password").get)
    properties
  }

}
