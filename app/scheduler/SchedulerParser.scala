package scheduler

/**
  * Created by chlr on 5/28/17.
  */
object SchedulerParser {

  def quartzToLinux(cron: String) = {
    cron.split("\\s").filterNot(_.length == 0) match {
      case Array(_, mins, hours, dom, month, dow) =>
        Array(mins, hours, dom, month, dow).mkString(" ")
    }
  }

  def linuxToQuartz(cron: String) = {
    cron.split("\\s").filterNot(_.length == 0) match {
      case Array(mins, hours, "*", month, "*") =>
        Array("0", mins, hours, "*", month, "?").mkString(" ")
      case Array(mins, hours, dom, month, dow) =>
        Array("0", mins, hours, dom, month, dow).mkString(" ")
    }
  }


}
