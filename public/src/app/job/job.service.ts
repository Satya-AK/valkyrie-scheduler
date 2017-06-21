import { Injectable } from '@angular/core';
import {Http} from "@angular/http";
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/operator/catch';
import 'rxjs/add/operator/map';


@Injectable()
export class JobService {

  constructor (private http: Http) {}

  errorJobs = [{ name: "error_job_1", desc: "this is a error job" }];

  /**
   * list jobs
   * @param groupId
   */
  getJobs(): Observable<Job[]> {
    return this.http.get("/job/list")
        .map(x => x.json())
        .catch(error => { console.log(error) ;return Observable.create([]) })
  }

}

export interface Job {

  name: String

  desc: String

}
