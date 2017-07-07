package model

import java.sql.Timestamp
import java.util.Date
import java.util.concurrent.TimeUnit

import util.Util.TimestampJsonParser
import play.api.libs.json.Json

/**
  * Created by chlr on 7/6/17.
  */
case class SchedulerState(schedulerName: String, instanceName: String, lastCheckIn: Long, checkInInterval: Long) {

  def isActive = {
    lastCheckIn > (new Date().getTime - checkInInterval)
  }

  def json = Json.obj("name" -> instanceName,
    "active" -> isActive,
    "last_checkin_time" -> new Timestamp(lastCheckIn),
    "checkin_interval" -> String.format(s"${TimeUnit.MILLISECONDS.toMinutes(checkInInterval)}m " +
      s"${TimeUnit.MILLISECONDS.toSeconds(checkInInterval)}s")
  )

}
