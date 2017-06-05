package controllers

import util.{AppSpec, GroupHelper}


/**
  * Created by chlr on 6/4/17.
  */

class GroupControllerSpec extends AppSpec with GroupHelper  {

  "GroupController" must {
    "create a group" in {
      createGroup("create_group_test")
    }
  }

  it must {
    "list groups" in {
      createGroup("list_group_test")
      listGroup.value.length must be > 1
      listGroup.value.seq.map(x => (x \ "name").as[String]) must contain ("list_group_test")
    }
  }

}
