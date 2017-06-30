
export class Trigger {

  checked: boolean = false;

  public jobName: string = null;

  constructor(public id: string, public name: string, public desc: string, public cron: string,
              public jobId: string) {}

  /**
   * create Trigger model from json
   * @param json
   * @returns {Trigger}
   */
  public static fromJson(json: any) {
    return new Trigger(json.id ,json.name, json.description, json.cron, json.job_id);
  }

  /**
   *
   * @returns {{id: string, trigger_name: string, description: string, cron: string, job_name: string}}
   */
  json() {
    return {
      id: this.id,
      name: this.name,
      description: this.desc,
      cron: this.cron,
      job_id: this.jobId,
    }
  }

}
