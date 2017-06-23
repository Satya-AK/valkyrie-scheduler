import { Injectable } from '@angular/core';
import {Http} from "@angular/http";
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/operator/catch';
import 'rxjs/add/operator/map';
import {Job} from "./job";
import {ApiError, APIResponse, APISuccess} from "../shared/api-error";
import {BaseApiService} from "../shared/base-api-service";


@Injectable()
export class JobService extends BaseApiService {

  constructor (private http: Http) {
    super();
  }

  errorJobs = [{ name: "error_job_1", desc: "this is a error job" }];

  /**
   * list jobs
   * @param groupId
   */
  getJobs(): Observable<Job[]> {
    return this.http.get("/job/list")
        .map(x => x.json().map( x => Job.fromJson(x)))
        .catch(x => this.handleError(x))
  }

  /**
   * create job
   * @param job
   * @returns {Observable<R>}
   */
  createJob(job: Job): Observable<string> {
    return this.http.post("/job/create/test", job.json())
          .map(x => "job created successfully")
  }

}

