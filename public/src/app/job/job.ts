

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
    public workDir: string
  ){}

  json() {
      return {
        id: this.id,
        name: this.name,
        desc: this.desc,
        command: this.command,
        working_dir: this.workDir
      }
  }

  static fromJson(json: any): Job {
      return new Job(json.id, json.name, json.desc, json.command, json.working_dir)
  }

}
