package scheduler

/**
  * Created by chlr on 5/29/17.
  */

trait ProcessCache {

  /**
    * get process for instanceId
    * @param instanceId
    * @return
    */
  def fetch(instanceId: String): Option[Process]

  /**
    * save process with instanceId
    * @param instanceId
    * @param process
    * @return
    */
  def save(instanceId: String, process: Process): Unit


  /**
    * remove process from cache
    * @param instanceId
    * @return
    */
  def remove(instanceId: String): Option[Process]

}