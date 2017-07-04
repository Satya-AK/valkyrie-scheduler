package scheduler

import java.util.concurrent.ConcurrentHashMap
import com.google.inject.Singleton
import play.api.Logger
import scala.collection.JavaConverters._

/**
  * Created by chlr on 6/1/17.
  */

/**
  * implementation of ProcessCache that uses `java.util.concurrent.ConcurrentHashMap`
  */
@Singleton
class ProcessCacheImpl extends ProcessCache {

  private val cache = new ConcurrentHashMap[String, Process]()

  val logger = Logger(getClass)

  /**
    * get process for instanceId
    * @param instanceId
    * @return
    */
  def fetch(instanceId: String): Option[Process] = {
    println(s"fetching instanceId $instanceId from processCache")
    logger.info(s"fetching process handler for instance id $instanceId")
    Option(cache.get(instanceId))
  }

  /**
    * save process with instanceId
    * @param instanceId
    * @param process
    * @return
    */
  def save(instanceId: String, process: Process) = {
    println(s"saving instanceId $instanceId from processCache")
    logger.info(s"saving process handler for instance id $instanceId")
    cache.put(instanceId, process)
  }

  /**
    * remove process from cache
    * @param instanceId
    * @return
    */
  def remove(instanceId: String) = {
    println(s"removing instanceId $instanceId from processCache")
    println(cache.asScala.mkString(","))
    logger.info(s"popping process handler for instance id $instanceId")
    Option(cache.remove(instanceId))
  }

}
