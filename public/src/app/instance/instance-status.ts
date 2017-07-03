export class InstanceStatus {

  constructor(public id: number, public name: string) {}

  static fromJson(json: any) {
    new InstanceStatus(json.id, json.name)
  }

  json() {
    return {
      id: this.id,
      name: this.name
    }
  }

  static status = [new InstanceStatus(1, "running"),
    new InstanceStatus(2,"succeeded"),
    new InstanceStatus(3,"failed"),
    new InstanceStatus(4,"error")];

}
