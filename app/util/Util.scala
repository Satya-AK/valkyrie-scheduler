package util

import java.util.Properties

/**
  * Created by chlr on 5/27/17.
  */
object Util {

  def hostName = java.net.InetAddress.getLocalHost.getHostName


  def configToProperties(config: play.api.Configuration) = {
    val properties = new Properties()
    config.keys.foreach(x => properties.setProperty(x, config.getString(x).get))
    properties
  }

}
