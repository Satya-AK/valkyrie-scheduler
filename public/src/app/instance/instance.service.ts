import { Injectable } from '@angular/core';
import {Http, Headers} from "@angular/http";
import {Instance} from "./instance";
import {BaseApiService} from "../shared/base-api-service";
import {Observable} from "rxjs/Observable";
import {InstanceQuery} from "./instance-query";

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

  /**
   * query instances
   * @param instanceQuery
   * @returns {Observable<R|T>}
   */
  query(instanceQuery: InstanceQuery): Observable<Instance[]> {
    return this.http
      .post("/instance/query",  instanceQuery.json())
      .map(x => x.json().map(ins => Instance.fromJson(ins)))
      .catch(err => this.handleError(err))
  }

  /**
   * kill instance
   * @param instanceId
   * @returns {Observable<R|T>}
   */
  kill(instanceId: string): Observable<string> {
    return this.http
      .post("/instance/kill/"+instanceId, {})
      .map(x => x.json().message)
      .catch(err => this.handleError(err))
  }


  forceFinish(instanceId: string): Observable<string> {
    return this.http
      .post("/instance/forcefinish/"+instanceId, {})
      .map(x => x.json().message)
      .catch(err => this.handleError(err))
  }

}
