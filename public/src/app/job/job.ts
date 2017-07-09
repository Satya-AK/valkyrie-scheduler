

/**
 * Job model class
 */
export class Job {

  checked: boolean = false;

  constructor(
    public id: string,
    public name: string,
    public desc: string,
    public command: string,
    public workDir: string,
    public emailOnFailure: boolean,
    public emailOnSuccess: boolean
  ){}

  json() {
      return {
        id: this.id,
        name: this.name,
        desc: this.desc,
        command: this.command,
        working_dir: this.workDir,
        email_on_failure: this.emailOnFailure,
        email_on_success: this.emailOnSuccess
      }
  }

  static fromJson(json: any): Job {
      return new Job(json.id,
        json.name,
        json.desc,
        json.command,
        json.working_dir,
        json.email_on_failure,
        json.email_on_success)
  }

}
