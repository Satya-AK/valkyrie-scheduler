

/**
 * Job model class
 */
export class Job {

  constructor(
    public name: string,
    public desc: string,
    public command: string,
    public workDir: string
  ){}

}
