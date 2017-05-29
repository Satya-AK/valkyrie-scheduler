package scheduler

import sys.process._

/**
  * Created by chlr on 5/29/17.
  */

class CommandExecutor(command: String) {

  def execute() = {
    val retCode = command.!
    assert(retCode == 0)
  }

}
