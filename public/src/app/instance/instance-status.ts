export class InstanceStatus {

  constructor(public id: number, public name: string) {}

  static status = [new InstanceStatus(1, "running"),
    new InstanceStatus(2,"succeeded"),
    new InstanceStatus(3,"failed"),
    new InstanceStatus(4,"error"),
    new InstanceStatus(5, "finished")
  ];


  static running = InstanceStatus.status.filter(x => x.name == "running")[0].id;
  static succeeded = InstanceStatus.status.filter(x => x.name == "succeeded")[0].id;
  static failed = InstanceStatus.status.filter(x => x.name == "failed")[0].id;
  static error = InstanceStatus.status.filter(x => x.name == "error")[0].id;
  static finished = InstanceStatus.status.filter(x => x.name == "finished")[0].id;

  static fromJson(json: any) {
    new InstanceStatus(json.id, json.name)
  }

  json() {
    return {
      id: this.id,
      name: this.name
    }
  }


}
