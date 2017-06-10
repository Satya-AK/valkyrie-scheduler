package util

import org.scalatest.BeforeAndAfterAll
import org.scalatestplus.play.{OneAppPerSuite, PlaySpec}
import play.api.Application
import repo.{AppInstanceLogRepository, AppInstanceRepository, TriggerRepository}
import scheduler.Scheduler

/**
  * Created by chlr on 6/4/17.
  */
trait AppSpec extends PlaySpec with OneAppPerSuite with BeforeAndAfterAll {


  lazy val triggerRepository = Application.instanceCache[TriggerRepository].apply(app)

  lazy val instanceRepository = Application.instanceCache[AppInstanceRepository].apply(app)

  lazy val instanceLogRepository = Application.instanceCache[AppInstanceLogRepository].apply(app)

  lazy val scheduler =  Application.instanceCache[Scheduler].apply(app)

  override def beforeAll() = {
    scheduler.pause()
  }

  override def afterAll() = {
    scheduler.destroy()
  }

}
