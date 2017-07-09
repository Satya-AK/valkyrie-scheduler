import { Injectable } from '@angular/core';
import {Http} from "@angular/http";
import {Trigger} from "./trigger";
import {BaseApiService} from "../shared/base-api-service";
import {GroupContextService} from "../group/group.service";

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
      .get("/trigger/list/group/"+this.groupContextService.getCurrentGroup().id)
      .map(x => x.json().map(y => { let trigger = Trigger.fromJson(y); trigger.jobName = y.job_name; return trigger }))
      .catch(err => this.handleError(err))
  }

  /**
   * fetch trigger by id
   * @param id
   * @returns {Observable<R|T>}
   */
  fetch(id: string) {
    return this.http
      .get("/trigger/fetch/group/"+this.groupContextService.getCurrentGroup().id+"/trigger/"+id)
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
      .post("/trigger/create/group/"+this.groupContextService.getCurrentGroup().id, trigger.json())
      .map(x => "trigger "+trigger.name+" created successfully")
      .catch(err => this.handleError(err))
  }

  /**
   *
   * @param trigger
   * @returns {Observable<R|T>}
   */
  remove(trigger: Trigger) {
    return this.http
      .post("/trigger/delete/group/"+this.groupContextService.getCurrentGroup().id+"/trigger/"+trigger.id,{})
      .map(x => "trigger "+trigger.name+" successfully deleted")
      .catch(err => this.handleError(err))
  }

  /**
   * disable trigger
   * @param trigger
   * @returns {Observable<R|T>}
   */
  disable(trigger: Trigger) {
    return this.http
      .post("/trigger/disable/group/"+this.groupContextService.getCurrentGroup().id+"/trigger/"+trigger.id, {})
      .map(x => "trigger "+trigger.name+" successfully disabled")
      .catch(err => this.handleError(err))
  }

  /**
   *
   * @param trigger
   */
  enable(trigger: Trigger) {
    return this.http
      .post("/trigger/enable/group/"+this.groupContextService.getCurrentGroup().id+"/trigger/"+trigger.id, {})
      .map(x => "trigger "+trigger.name+" successfully disabled")
      .catch(err => this.handleError(err))
  }

  /**
   *
   * @param trigger
   */
  update(trigger: Trigger) {
    return this.http
      .post("/trigger/update/group/"+this.groupContextService.getCurrentGroup().id, trigger.json())
      .map(x => x.json().message)
      .catch(err => this.handleError(err))
  }

}
