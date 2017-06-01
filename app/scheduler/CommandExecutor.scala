package scheduler

import java.io.File
import java.util.StringTokenizer
import util.AppException.{JobExecutionException, JobSetUpException}
import scala.collection.JavaConverters._
import scala.util.{Failure, Success, Try}
import CommandExecutor.Command

/**
  * Created by chlr on 5/29/17.
  */

class CommandExecutor(command: Command, processCache: ProcessCache) {

  /**
    * build process builder
    * @return
    */
  private val processBuilder = {
    println(new File(command.tmpDir, "stdout.log"))
    val processBuilder = new ProcessBuilder(parseCommand)
      .directory(new File(command.workingDir))
      .redirectOutput(new File(command.tmpDir, "stdout.log"))
      .redirectError(new File(command.tmpDir, "stderr.log"))
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
      createTempDir()
      processBuilder
  }


  /**
    * execute command
    * @return
    */
  def execute() = {
    setupJob.flatMap(_ => run) match {
      case _ => processCache.remove(command.instanceId); ()
    }
  }

  /**
    * run process
    * @return
    */
  private def run = {
    Try(processBuilder.start()) match {
      case Success(process) =>
        processCache.save(command.instanceId, process)
        process.waitFor() match {
          case 0 => Success(0)
          case retCode => Failure(new JobExecutionException(s"command failed with return code $retCode"))
        }
      case Failure(th) => Failure(new JobExecutionException(th.getMessage))
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

}
