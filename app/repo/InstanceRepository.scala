package repo

import com.google.inject.Inject
import model.{AppStatus, Instance}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile
import table.InstanceTable
import util.Util.{uuid, _}
import StatusRepository.Status
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by chlr on 5/29/17.
  */

class InstanceRepository @Inject() (protected val dbConfigProvider: DatabaseConfigProvider,
                                    protected val instanceTable: InstanceTable,
                                    protected val statusRepository: StatusRepository)
 extends HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._


  /**
    *
    * @param jobName
    * @param groupName
    * @param triggerName
    */
  def createInstance(jobName: String, groupName: String, triggerName: Option[String]): Future[String] = {
    val instanceId = uuid
    def query(status: AppStatus, instanceId: String) = instanceTable.table
      .filter(x => x.jobName === jobName && x.groupName === groupName)
      .sortBy(_.seqId.desc).map(_.seqId).result.headOption flatMap {
       x => instanceTable.table +=
         Instance(instanceId, groupName, jobName, triggerName, currentTimeStamp, None,
           x.getOrElse(0L)+1, status.id, 1, hostName)
    }
    for {
      status <- statusRepository.getStatusName(Status.running)
      _ <- db.run(query(status, instanceId).transactionally).map(_ => uuid)
    } yield instanceId
  }



}
