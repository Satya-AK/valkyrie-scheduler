
export class InstanceQuery {

  constructor(public startDate: string,
              public endDate: string,
              public status: number) {}

  static fromJson(json: any) {
    new InstanceQuery(json.start_date, json.end_date, json.status)
  }

  json() {
    return {
      start_date: this.startDate,
      end_date: this.endDate,
      status: this.status?this.status:null
    }
  }

}
