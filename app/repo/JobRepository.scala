package repo

import com.google.inject.Inject
import model.AppJob
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile
import table.JobTable
import util.AppException.EntityNotFoundException

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
    * @param jobName
    * @param groupId
    * @return
    */
  def getJob(groupId: String, jobName: String) = {
    db.run(jobTable.table
      .filter(x => x.groupId === groupId && x.jobName === jobName)
      .result.headOption) flatMap {
      case Some(x) => Future.successful(AppJob.create(x))
      case None => Future.failed(new EntityNotFoundException(s"job $jobName not not found in group $groupId"))
    }
  }



  def listJobs(groupId: String) = {
    db.run(jobTable.table.filter(x => x.groupId === groupId).result)
    .map(x => x.map(x => AppJob.create(x)))
  }





}
