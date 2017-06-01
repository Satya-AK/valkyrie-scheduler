package scheduler

import java.util.concurrent.ConcurrentHashMap

/**
  * Created by chlr on 6/1/17.
  */

/**
  * implementation of ProcessCache that uses `java.util.concurrent.ConcurrentHashMap`
  */
class ProcessCacheImpl extends ProcessCache {

  private val cache = new ConcurrentHashMap[String, Process]()

  /**
    * get process for instanceId
    * @param instanceId
    * @return
    */
  def fetch(instanceId: String): Option[Process] = {
    Option(cache.get(instanceId))
  }

  /**
    * save process with instanceId
    * @param instanceId
    * @param process
    * @return
    */
  def save(instanceId: String, process: Process) = {
    cache.put(instanceId, process)
  }

  /**
    * remove process from cache
    * @param instanceId
    * @return
    */
  def remove(instanceId: String) = {
    Option(cache.remove(instanceId))
  }

}
