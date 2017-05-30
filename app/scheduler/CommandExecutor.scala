package scheduler

import java.io.File
import java.util.StringTokenizer

import util.AppException.{JobExecutionException, JobSetUpException}

import scala.collection.JavaConverters._
import scala.util.{Failure, Success, Try}

/**
  * Created by chlr on 5/29/17.
  */

class CommandExecutor(instanceId: String,
                      command: String,
                      workingDir: String,
                      tmpDir: String,
                      env: Map[String, String]) {


  /**
    * build process builder
    * @return
    */
  private def processBuilder = {
    val processBuilder = new ProcessBuilder(parseCommand)
      .directory(new File(workingDir))
      .redirectOutput(new File(workingDir, "stdout"))
      .redirectError(new File(workingDir, "stderr"))
    env.foreach({case (key, value) => processBuilder.environment.put(key, value)})
    processBuilder
  }

  /**
    * create temporary directory
    */
  private def createTempDir() = {
    if(! new File(tmpDir).mkdirs()) {
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
      case _ => ProcessCache.remove(instanceId); ()
    }
  }

  /**
    * run process
    * @return
    */
  private def run = {
    Try(processBuilder.start()) match {
      case Success(process) =>
        ProcessCache.save(instanceId, process)
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
    val tokenizer = new StringTokenizer(command)
    (0 to tokenizer.countTokens).map(_ => tokenizer.nextToken()).toList.asJava
  }

}
