
export class Trigger {

  constructor(public name: string, public desc: string, public cron: string, public jobName: string) {}

  /**
   * create Trigger model from json
   * @param json
   * @returns {Trigger}
   */
  public static fromJson(json: any) {
    return new Trigger(json.trigger_name, json.description, json.cron, json.job_name);
  }

  /**
   *
   * @returns {{trigger_name: string, description: string, cron: string, job_name: string}}
   */
  json() {
    return {
      name: this.name,
      description: this.desc,
      cron: this.cron,
      job_name: this.jobName,
    }
  }

}
