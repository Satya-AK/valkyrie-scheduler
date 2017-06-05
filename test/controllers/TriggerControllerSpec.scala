package controllers

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

}
