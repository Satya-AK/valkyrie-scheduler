import { Injectable } from '@angular/core';
import {Http} from "@angular/http";
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/operator/catch';
import 'rxjs/add/operator/map';
import {Job} from "./job";
import {BaseApiService} from "../shared/base-api-service";
import {GroupContextService} from "../shared/group-context.service";


@Injectable()
export class JobService extends BaseApiService {

  constructor(private http: Http,
              private groupContextService: GroupContextService) {
    super();
  }


  /**
   * get job by name.
   * @param jobId
   * @returns {any}
   */
  fetch(jobId: string): Observable<Job> {
    return this.http
      .get("/job/fetch/group/"+this.groupContextService.getCurrentGroup().id+"/job/"+jobId)
      .map(x => Job.fromJson(x.json()))
      .catch(err => this.handleError(err));
  }

  /**
   * delete job by name
   * @param jobId
   * @returns {Observable<R|T>}
   */
  remove(jobId: string): Observable<any> {
    return this.http
      .post("/job/delete/group/"+this.groupContextService.getCurrentGroup().id+"/job/"+jobId, {})
      .map(x => x.json().message)
      .catch(err => this.handleError(err));
  }


  /**
   * update job
   * @param job
   * @returns {Observable<R|T>}
   */
  update(job: Job): Observable<string> {
    return this.http
      .post("/job/update/group/"+this.groupContextService.getCurrentGroup().id, job.json())
      .map(x => x.json().message)
      .catch(err => this.handleError(err))
  }


  /**
   * list jobs
   * @param groupId
   */
  list(): Observable<Job[]> {
    return this.http.get("/job/list/group/"+this.groupContextService.getCurrentGroup().id)
      .map(x => x.json().map( x => Job.fromJson(x)))
      .catch(x => this.handleError(x))
  }

  /**
   * create job
   * @param job
   * @returns {Observable<R>}
   */
  create(job: Job): Observable<string> {
    return this.http.post("/job/create/"+this.groupContextService.getCurrentGroup().id, job.json())
          .map(x => "job created successfully")
          .catch(x => this.handleError(x))
  }

}

