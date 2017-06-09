package controllers

import play.api.libs.json.JsArray
import play.api.test.FakeRequest
import play.api.test.Helpers._
import util.{AppSpec, GroupHelper, JobHelper, TriggerHelper}

/**
  * Created by chlr on 6/4/17.
  */
class TriggerControllerSpec extends AppSpec with JobHelper with GroupHelper with TriggerHelper {

  override def beforeAll() = {
    createGroup(testTriggerGroup)
  }

  "TriggerController" must {
    "create a Trigger" in {
      createJob("job_create_trigger_test", testTriggerGroup)
      createTrigger("tgr_job_create_trigger_test", testTriggerGroup, "job_create_trigger_test")
    }
  }

  it must {
    "fetch a Trigger" in {
      createJob("job_fetch_trigger_test", testTriggerGroup)
      createTrigger("tgr_job_fetch_trigger_test", testTriggerGroup, "job_fetch_trigger_test")
      val Some(result) = route(app, FakeRequest(GET,
        controllers.routes.TriggerController.fetchTrigger(testTriggerGroup, "tgr_job_fetch_trigger_test").path))
      status(result) must be (OK)
      val json = contentAsJson(result)
      (json \ "trigger_name").as[String] must be ("tgr_job_fetch_trigger_test")
      (json \ "job_name").as[String] must be ("job_fetch_trigger_test")
    }
  }

  it must {
    "list all triggers" in {
      createJob("job_list_trigger_test", testTriggerGroup)
      createTrigger("tgr_job_list_trigger_test", testTriggerGroup, "job_list_trigger_test")
      val Some(result) = route(app, FakeRequest(GET,
        controllers.routes.TriggerController.listTriggers(testTriggerGroup).path))
      status(result) must be (OK)
      contentAsJson(result).as[JsArray].value.map(x => (x \ "trigger_name").as[String]) must
        contain ("tgr_job_list_trigger_test")
    }
  }

}
