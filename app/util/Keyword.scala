package util

/**
  * Created by chlr on 5/29/17.
  */

object Keyword {

  object JobData {
    val command = "command"
    val workingDir = "working_dir"
    val environment = "environment"
    val jobName = "name"
    val emailOnFailure = "email_on_failure"
    val emailOnSuccess = "email_on_success"
  }

  object AppSetting {
    val tmpDir = "directory"
    val hostName = "hostname"
  }

  object AppLog {
    val stdout = "stdout"
    val stderr = "stderr"
  }

  object EmailSetting {
    val rootKey = "app.email"
    val hostName = "hostname"
    val userName = "username"
    val password = "password"
    val port = "port"
    val tls = "tls"
  }

}
