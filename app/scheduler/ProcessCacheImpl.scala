package scheduler

import java.util.concurrent.ConcurrentHashMap
import scala.collection.JavaConverters._
import com.google.inject.Singleton
import play.api.Logger

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
    logger.info(s"saving process handler for instance id $instanceId")
    cache.put(instanceId, process)
  }

  /**
    * remove process from cache
    * @param instanceId
    * @return
    */
  def remove(instanceId: String) = {
    println(cache.keys().asScala.toList)
    logger.info(s"popping process handler for instance id $instanceId")
    Option(cache.remove(instanceId))
  }

}
