package repo

import com.google.inject.Inject
import model.AppJob
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile
import table.JobTable
import util.AppException.{EntityNotFoundException, InvalidStateException}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by chlr on 5/27/17.
  */

class JobRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider,
                              protected val jobTable: JobTable)
  extends HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._

  /**
    * get job
    * @param jobId
    * @param groupId
    * @return
    */
  def getJob(groupId: String, jobId: String) = {
    db.run(jobTable.table
      .filter(x => x.groupId === groupId && x.jobName === jobId)
      .result.headOption) flatMap {
      case Some(x) => Future.successful(AppJob.create(x))
      case None => Future.failed(new EntityNotFoundException(s"job $jobId not not found in group $groupId"))
    }
  }

  /**
    * check if trigger exists with a name in group while create
    * @param groupId
    * @param jobName
    * @return
    */
  def checkJobNameForCreate(groupId: String, jobName: String): Future[Unit] = {
    listJobs(groupId).map(x => x.find(_.jobName == jobName)) flatMap {
      case Some(_) => Future.failed(new InvalidStateException(s"job with name $jobName already exists"))
      case None => Future.successful(())
    }
  }

  /**
    *
    * @param groupId
    * @param jobId
    * @param jobName
    * @return
    */
  def checkJobNameForUpdate(groupId: String, jobId: String, jobName: String): Future[Unit] = {
    listJobs(groupId).map(x => x.filter(_.jobName == jobName).toList) flatMap {
      case x :: Nil if x.id == jobId => Future.successful(())
      case Nil => Future.successful(())
      case _ => Future.failed(new InvalidStateException(s"job with name $jobName already exists"))
    }
  }


  /**
    * list jobs in group
    * @param groupId
    * @return
    */
  def listJobs(groupId: String): Future[Seq[AppJob]] = {
    db.run(jobTable.table.filter(x => x.groupId === groupId).result)
    .map(x => x.map(x => AppJob.create(x)))
  }





}
