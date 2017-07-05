package scheduler

import java.util.concurrent.ConcurrentHashMap

import com.google.inject.Singleton
import org.apache.commons.exec.ExecuteWatchdog
import play.api.Logger

/**
  * Created by chlr on 6/1/17.
  */

/**
  * implementation of ProcessCache that uses `java.util.concurrent.ConcurrentHashMap`
  */
@Singleton
class ProcessCacheImpl extends ProcessCache {

  override val cache = new ConcurrentHashMap[String, ExecuteWatchdog]()

  val logger = Logger(getClass)

  /**
    * get process for instanceId
    * @param instanceId
    * @return
    */
  def fetch(instanceId: String): Option[ExecuteWatchdog] = {
    logger.info(s"fetching process handler for instance id $instanceId")
    Option(cache.get(instanceId))
  }

  /**
    * save process with instanceId
    * @param instanceId
    * @param process
    * @return
    */
  def save(instanceId: String, process: ExecuteWatchdog) = {
    logger.info(s"saving process handler for instance id $instanceId")
    cache.put(instanceId, process)
  }

  /**
    * remove process from cache
    * @param instanceId
    * @return
    */
  def remove(instanceId: String) = {
    logger.info(s"popping process handler for instance id $instanceId")
    Option(cache.remove(instanceId))
  }

}
