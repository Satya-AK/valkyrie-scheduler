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

  constructor (private http: Http,
               private groupContextService: GroupContextService) {
    super();
  }

  errorJobs = [{ name: "error_job_1", desc: "this is a error job" }];


  getJobByName(jobName: string): Observable<Job> {
     if (this.groupContextService.getCurrentGroup()) {
       return this.http
         .get("/job/fetch/group/"+this.groupContextService.getCurrentGroup().name+"/job/"+jobName)
         .map(x => Job.fromJson(x.json()))
         .catch(err => this.handleError(err));
     } else {
       return Observable.of(new Job("", "", "", ""));
     }
  }


  /**
   * list jobs
   * @param groupId
   */
  getJobs(): Observable<Job[]> {
    if (this.groupContextService.getCurrentGroup()) {
      return this.http.get("/job/list/group/"+this.groupContextService.getCurrentGroup().name)
        .map(x => x.json().map( x => Job.fromJson(x)))
        .catch(x => this.handleError(x))
    } else {
      return Observable.of([]);
    }
  }

  /**
   * create job
   * @param job
   * @returns {Observable<R>}
   */
  createJob(job: Job): Observable<string> {
    return this.http.post("/job/create/"+this.groupContextService.getCurrentGroup().name, job.json())
          .map(x => "job created successfully")
          .catch(x => this.handleError(x))
  }

}

