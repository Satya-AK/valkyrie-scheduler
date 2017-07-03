import {InstanceStatus} from "./instance-status";
/**
 * instance model class
 */
export class Instance {

  constructor(public id: string,
              public groupId: string,
              public jobId: string,
              public jobName: string,
              public triggerId: string,
              public triggerName: string,
              public startTime: string,
              public endTime: string,
              public message: string,
              public returnCode: number,
              public seqId: number,
              public status: number,
              public attempt: string,
              public agent: string) {}


  statusName(): string {
    return InstanceStatus.status.filter(x => x.id == this.status).map(x => x.name)[0]
  }

  /**
   * build instance from JSON
   * @param data
   * @returns {Instance}
   */
 static fromJson(data: any) {
    return new Instance(data.id,
      data.group_id,
      data.job_id,
      data.job_name,
      data.trigger_id,
      data.trigger_name,
      data.start_time,
      data.end_time,
      data.message,
      data.return_code,
      data.seq_id,
      data.status,
      data.attemp,
      data.agent
    )
 }

  /**
   * convert Instance to JSON
   * @returns {{id: string, group_id: string, job_id: string, triggerId: string, start_time: string, end_time: string, message: string, return_code: number, seq_id: number, status: string, attempt: string, agent: string}}
   */
 json() {
    return {
      id: this.id,
      group_id: this.groupId,
      job_id: this.jobId,
      triggerId: this.triggerId,
      start_time: this.startTime,
      end_time: this.endTime,
      message: this.message,
      return_code: this.returnCode,
      seq_id: this.seqId,
      status: this.status,
      attempt: this.attempt,
      agent: this.agent
    }
 }

}


