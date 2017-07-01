import { Injectable } from '@angular/core';
import {Http} from "@angular/http";
import {Instance} from "./instance";
import {BaseApiService} from "../shared/base-api-service";
import {Observable} from "rxjs/Observable";

@Injectable()
export class InstanceService extends BaseApiService {

  constructor(private http: Http) {
    super();
  }

  /**
   * list instances of a job
   * @param jobId
   * @returns {Observable<R|T>}
   */
  listJobInstance(jobId: string): Observable<Instance[]> {
    return this.http
      .get("/instance/job/list/job/"+jobId)
      .map(x => x.json().map(y => Instance.fromJson(y)))
      .catch(err => this.handleError(err))
  }

  /**
   *
   * @param instanceId
   * @returns {Observable<R|T>}
   */
  fetchLog(instanceId: string) {
    return this.http
      .get("/instance/logs/"+ instanceId)
      .map(x => x.json())
      .catch(err => this.handleError(err))
  }

  /**
   *
   * @param instanceId
   * @returns {Observable<R|T>}
   */
  fetch(instanceId: string): Observable<Instance> {
    return this.http
      .get("/instance/fetch/"+instanceId)
      .map(x => Instance.fromJson(x.json()))
      .catch(err => this.handleError(err))
  }

}
