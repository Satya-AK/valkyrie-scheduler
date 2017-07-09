package model

import play.api.Configuration

/**
  * Created by chlr on 7/7/17.
  */

/**
  * Email connection settings
  * @param fromEmail
  * @param hostname
  * @param username
  * @param password
  * @param port
  * @param tls
  */
case class AppEmailConnection(fromEmail: String,
                              hostname: String,
                              username: String,
                              password: String,
                              port: Int,
                              tls: Boolean) {

}

object AppEmailConnection {

  def create(configuration: Configuration) = {
    import util.Keyword.EmailSetting._
    AppEmailConnection(
      configuration.getString(userName).get,
      configuration.getString(hostName).get,
      configuration.getString(userName).get,
      configuration.getString(password).get,
      configuration.getInt(port).get,
      configuration.getBoolean(tls).get
    )
  }
}
