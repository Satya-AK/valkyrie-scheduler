package scheduler

import java.io.File
import java.util.StringTokenizer
import model.AppInstanceLog
import scheduler.CommandExecutor.{Command, CommandResponse}
import util.AppException.{JobExecutionException, JobSetUpException}
import util.{Keyword, Util}
import scala.collection.JavaConverters._
import scala.io.Source
import scala.util.{Failure, Success, Try}

/**
  * Created by chlr on 5/29/17.
  */

class CommandExecutor(command: Command, processCache: ProcessCache) {


  private val stdoutFile = new File(Util.instanceLogDirectory(command.instanceId), "stdout.log")

  private val stderrFile = new File(Util.instanceLogDirectory(command.instanceId), "stderr.log")

  /**
    * build process builder
    * @return
    */
  private val processBuilder = {
    val processBuilder = new ProcessBuilder(parseCommand)
      .directory(new File(command.workingDir))
      .redirectOutput(stdoutFile)
      .redirectError(stderrFile)
    command.env.foreach({case (key, value) => processBuilder.environment.put(key, value)})
    processBuilder
  }

  /**
    * create temporary directory
    */
  private def createTempDir() = {
    if(! new File(command.tmpDir).mkdirs()) {
      throw new JobSetUpException("failed to create tmp directory")
    }
  }

  /**
    * setup job
    * @return
    */
  private def setupJob = Try {
    stdoutFile.getParentFile.mkdirs(); stdoutFile.createNewFile()
    stderrFile.getParentFile.mkdirs(); stderrFile.createNewFile()
    processBuilder
  }


  /**
    * execute command
    * @return
    */
  def execute(): CommandResponse = {
    setupJob.flatMap(_ => run) match {
      case Success(_) =>
        processCache.remove(command.instanceId); makeLogResponse()
      case Failure(th) => processCache.remove(command.instanceId); makeLogResponse(Some(th))
    }
  }

  /**
    * make log response object
    * @return
    */
  private def makeLogResponse(throwable: Option[Throwable]=None) = {
    val stdout = AppInstanceLog(command.instanceId,
      Keyword.AppLog.stdout,
      if(stdoutFile.exists) Some(Source.fromFile(stdoutFile).mkString) else None)
    val stderr = AppInstanceLog(command.instanceId,
      Keyword.AppLog.stderr,
      if(stdoutFile.exists) Some(Source.fromFile(stderrFile).mkString) else None)
    CommandResponse(stdout, stderr, throwable)
  }

  /**
    * run process
    * @return
    */
  private def run  = {
    Try(processBuilder.start()) match {
      case Success(process) =>
        processCache.save(command.instanceId, process)
        process.waitFor() match {
          case 0 => Success(0)
          case retCode => Failure(new JobExecutionException(retCode, s"command failed with return code $retCode"))
        }
      case Failure(th) => Failure(new JobExecutionException(-1, th.getMessage))
    }
  }

  /**
    * convert command string to array string
    * @return
    */
  private def parseCommand = {
    val tokenizer = new StringTokenizer(command.command)
    (0 until tokenizer.countTokens).map(_ => tokenizer.nextToken()).toList.asJava
  }

}

object CommandExecutor {

  /**
    * Command info object
    * @param instanceId
    * @param command
    * @param workingDir
    * @param tmpDir
    * @param env
    */
  case class Command(instanceId: String,
                     command: String,
                     workingDir: String,
                     tmpDir: String,
                     env: Map[String, String])


  /**
    *
    * @param stdout
    * @param stderr
    * @param exception
    */
  case class CommandResponse(stdout: AppInstanceLog,
                             stderr: AppInstanceLog,
                             exception: Option[Throwable] = None)

}
