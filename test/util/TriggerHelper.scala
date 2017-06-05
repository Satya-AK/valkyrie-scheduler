package util

import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers._

/**
  * Created by chlr on 6/4/17.
  */
trait TriggerHelper {

  self : AppSpec =>

  val testTriggerGroup = "test_trigger_group"


  def createTriggerJson(triggerName: String,
                        jobName: String) = {
    Json parse
      s"""
        |{
        |  "trigger_name": "$triggerName",
        |  "job_name": "$jobName",
        |  "cron": "*/2 * * * *",
        |  "description": "test trigger for $jobName"
        |}
      """.stripMargin
  }

  def createTrigger(triggerName: String,
                    groupName: String = testTriggerGroup,
                    jobName: String) = {
    val Some(result) = route(app, FakeRequest(POST,
      controllers.routes.TriggerController.createTrigger(groupName).path())
      .withJsonBody(createTriggerJson(triggerName, jobName)))
    status(result) must be (OK)
  }

}
