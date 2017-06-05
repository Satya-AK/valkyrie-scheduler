package util

import play.api.libs.json.Json
import play.api.test.FakeRequest
import play.api.test.Helpers._

/**
  * Created by chlr on 6/4/17.
  */

trait JobHelper {

  self : AppSpec =>

  val jobTestGroup = "job_test_group"


  def jobJson(jobName: String,
              groupName: String = jobTestGroup,
              command: String = "echo hello world") = {
    Json parse
      s"""
        |{
        |  "job_name": "$jobName",
        |  "group_name": "$groupName",
        |  "command": "$command",
        |  "description": "this is a test job"
        |}
      """.stripMargin
  }

  /**
    *
    * @param jobName
    * @param groupName
    * @param command
    */
  def createJob(jobName: String,
                groupName: String = jobTestGroup,
                command: String = "echo hello world") = {
    val Some(result) = route(app,
      FakeRequest(POST, controllers.routes.JobController.createJob(groupName).path())
        .withJsonBody(jobJson(jobName, groupName, command)))
    status(result) must be (OK)
  }


}
