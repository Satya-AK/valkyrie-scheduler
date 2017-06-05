package controllers

import play.api.libs.json.JsArray
import play.api.test.FakeRequest
import play.api.test.Helpers._
import util.{AppSpec, GroupHelper, JobHelper}

/**
  * Created by chlr on 6/4/17.
  */
class JobControllerSpec extends AppSpec with JobHelper with GroupHelper  {


  override def beforeAll() = {
    createGroup(jobTestGroup)
  }

  "JobController" must {
    "create job" in {
      createJob("job_create_test")
    }
  }

  it must {
    "list job" in {
      createJob("job_list_test")
      val Some(result) = route(app, FakeRequest(GET, controllers.routes.JobController.listJobs(jobTestGroup).path()))
      status(result) must be (OK)
      val json = contentAsJson(result).as[JsArray].value
        .map(x => (x \ "job_name").as[String]) must contain ("job_list_test")
    }
  }

  it must {
    "fetch job" in {
      createJob("job_fetch_test")
      val Some(result) = route(app, FakeRequest(GET,
        controllers.routes.JobController.fetchJob(jobTestGroup, "job_fetch_test").path()))
      val json = contentAsJson(result)
      (json \ "job_name").as[String] must be ("job_fetch_test")
      (json \ "command").as[String] must be ("echo hello world")
    }
  }
}
