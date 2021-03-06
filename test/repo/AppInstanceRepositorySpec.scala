package repo

import util.AppSpec
import util.Util._
import play.api.test.Helpers._
/**
  * Created by chlr on 6/10/17.
  */
class AppInstanceRepositorySpec extends AppSpec {

  "AppInstanceRepository" must {
    "create an instance" in {
      app
      val instanceId = uuid
      await(instanceRepository
      .createInstance(instanceId, "instance_repo_test_job", "instance_repo_test_group", None))
      val startInstance = await(instanceRepository.fetchInstance(instanceId))
      startInstance.jobId must be ("instance_repo_test_job")
      startInstance.groupId must be ("instance_repo_test_group")
      startInstance.statusId must be (1)
    }
  }

}
