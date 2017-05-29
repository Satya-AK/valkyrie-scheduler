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
    * @param groupName
    * @return
    */
  def getJob(jobName: String, groupName: String) = {
    db.run(jobTable.table
      .filter(x => x.groupName === groupName && x.jobName === jobName)
      .result.headOption) flatMap {
      case Some(x) => Future.successful(AppJob.create(x))
      case None => Future.failed(new EntityNotFoundException(s"job $jobName not not found in group $groupName"))
    }
  }




  def listJobs(groupName: String) = {
    db.run(jobTable.table.filter(x => x.groupName === groupName).result)
    .map(x => x.map(x => AppJob.create(x)))
  }





}
