package scheduler

import java.io._

import _root_.util.AppException.JobExecutionException
import _root_.util.{Keyword, Util}
import model.AppInstanceLog
import org.apache.commons.exec._
import scheduler.CommandExecutor.{Command, CommandResponse}

import scala.collection.JavaConverters._
import scala.io.Source
import scala.util.{Failure, Success, Try}

/**
  * Created by chlr on 5/29/17.
  */

class CommandExecutor(command: Command, processCache: ProcessCache) {


  private val stderrFile = new File(Util.instanceLogDirectory(command.instanceId), "stderr.log")
  private val stdoutFile = new File(Util.instanceLogDirectory(command.instanceId), "stdout.log")
  private lazy val stdoutFileStream = new FileOutputStream(stdoutFile)
  private lazy val stderrFileStream = new FileOutputStream(stderrFile)
  private val errInputStreamSink = new PipedInputStream()
  private val outInputStreamSink = new PipedInputStream()
  private val errInputStream = new PipedOutputStream(errInputStreamSink)
  private val outOutputStream = new PipedOutputStream(outInputStreamSink)

  private val processBuilder = {
    val executor = new DefaultExecutor()
    executor.setWorkingDirectory(new File(command.workingDir))
    executor.setStreamHandler(new PumpStreamHandler(outOutputStream, errInputStream))
    executor.setWatchdog(new ExecuteWatchdog(ExecuteWatchdog.INFINITE_TIMEOUT))
    executor
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
    val executor = processBuilder
    processCache.save(command.instanceId, executor.getWatchdog)
    new Thread(new StreamPumper(outInputStreamSink, stdoutFileStream)).start()
    new Thread(new StreamPumper(errInputStreamSink, stderrFileStream)).start()
    Try(executor.execute(CommandLine.parse(command.command), command.env.asJava)) match {
      case Success(0) => Success(0)
      case Success(x) => Failure(new JobExecutionException(x, s"command failed with return code $x"))
      case Failure(th: ExecuteException) => Failure(new JobExecutionException(th.getExitValue, th.getMessage))
    }
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
