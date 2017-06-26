

/**
 * Job model class
 */
export class Job {

  checked: boolean = false;

  constructor(
    public name: string,
    public desc: string,
    public command: string,
    public workDir: string
  ){}

  json() {
      return {
        name: this.name,
        desc: this.desc,
        command: this.command,
        workdir: this.workDir
      }
  }

  static fromJson(json: any): Job {
      return new Job(json.name, json.desc, json.command, "/var/tmp")
  }

}
