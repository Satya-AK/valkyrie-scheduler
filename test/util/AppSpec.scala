package util

import org.scalatest.BeforeAndAfterAll
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application
import repo.TriggerRepository
import scheduler.Scheduler

/**
  * Created by chlr on 6/4/17.
  */
trait AppSpec extends PlaySpec with GuiceOneAppPerSuite with BeforeAndAfterAll {


  lazy val triggerRepository = Application.instanceCache[TriggerRepository].apply(app)

  lazy val scheduler =  Application.instanceCache[Scheduler].apply(app)

  override def beforeAll() = {
    scheduler.pause()
  }

  override def afterAll() = {
    scheduler.destroy()
  }

}
