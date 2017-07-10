import { Injectable } from '@angular/core';
import {Http} from "@angular/http";
import {BaseApiService} from "./base-api-service";
import {Observable} from "rxjs/Observable";

@Injectable()
export class OtherService extends BaseApiService {

  constructor(private http: Http) {
    super();
  }

  agents(): Observable<Agent[]> {
    return this.http
      .get("/scheduler/agents")
      .map(x => x.json().map(y => Agent.fromJson(y)))
      .catch(err => this.handleError(err))
  }
}

export class Agent {

  constructor(public name: string, public active: boolean, public lastCheckInTime: string,
              public checkInInterval: string) {}

  static fromJson(json: any): Agent {
    return new Agent(json.name, json.active, json.last_checkin_time, json.checkin_interval)
  }

  json() {
    return {
      name: this.name,
      active: this.active,
      last_checkin_time: this.lastCheckInTime,
      checkin_interval: this.checkInInterval
    }
  }

}
