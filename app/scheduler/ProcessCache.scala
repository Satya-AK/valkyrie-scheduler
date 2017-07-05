package scheduler

import java.util.concurrent.ConcurrentHashMap

import org.apache.commons.exec.ExecuteWatchdog

/**
  * Created by chlr on 5/29/17.
  */

trait ProcessCache {

  val cache = new ConcurrentHashMap[String, ExecuteWatchdog]()

  /**
    * get process for instanceId
    * @param instanceId
    * @return
    */
  def fetch(instanceId: String): Option[ExecuteWatchdog]

  /**
    * save process with instanceId
    * @param instanceId
    * @param process
    * @return
    */
  def save(instanceId: String, process: ExecuteWatchdog): Unit


  /**
    * remove process from cache
    * @param instanceId
    * @return
    */
  def remove(instanceId: String): Option[ExecuteWatchdog]

}
