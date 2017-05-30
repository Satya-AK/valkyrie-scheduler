package scheduler

import java.util.concurrent.ConcurrentHashMap

/**
  * Created by chlr on 5/29/17.
  */
object ProcessCache {

  private val cache = new ConcurrentHashMap[String, Process]()

  /**
    * get process for instanceId
    * @param instanceId
    * @return
    */
  def fetch(instanceId: String): Process = {
    cache.get(instanceId)
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
    cache.remove(instanceId)
  }

}
