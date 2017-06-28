import { Injectable } from '@angular/core';
import {Http} from "@angular/http";
import {GroupContextService} from "../shared/group-context.service";
import {Trigger} from "./trigger";
import {BaseApiService} from "../shared/base-api-service";

@Injectable()
export class TriggerService extends BaseApiService {

  constructor(private http: Http,
              private groupContextService: GroupContextService) {
    super();
  }

  /**
   * list triggers in group
   */
  list() {
    return this.http
      .get("/trigger/list/group/"+this.groupContextService.getCurrentGroup().name)
      .map(x => x.json().map(y => Trigger.fromJson(y)))
      .catch(err => this.handleError(err))
  }

  /**
   * fetch trigger by name
   * @param name
   * @returns {Observable<R|T>}
   */
  fetch(name: string) {
    return this.http
      .get("/trigger/group/"+this.groupContextService.getCurrentGroup().name+"/trigger/"+name)
      .map(x => Trigger.fromJson(x.json()))
      .catch(err => this.handleError(err))
  }


  /**
   * create trigger
   * @param trigger
   * @returns {Observable<R|T>}
   */
  create(trigger: Trigger) {
    return this.http
      .post("/trigger/create/group/"+this.groupContextService.getCurrentGroup().name, trigger.json())
      .map(x => "trigger "+trigger.name+" created successfully")
      .catch(err => this.handleError(err))
  }

}
