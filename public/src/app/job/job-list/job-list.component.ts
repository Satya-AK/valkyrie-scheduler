import { Component, OnInit } from '@angular/core';
import {Job, JobService} from "../job.service";


@Component({
  selector: 'app-job-list',
  templateUrl: './job-list.component.html',
  styleUrls: ['./job-list.component.scss']
})
export class JobListComponent implements OnInit {

  rows: Job[] = [];

  constructor(private jobService: JobService) { }

  dtOptions: DataTables.Settings = {
    pagingType: 'full_numbers',
    columns: [{ orderable: true}, {orderable: true}]
  };


  ngOnInit(): void {
    this.fetchJobs();
    console.log("init called");
  }

  /**
   * fetch jobs from service
   */
  fetchJobs() {
    this.jobService
      .getJobs()
      .subscribe(rows => { console.log("I am called"); console.log(rows) ;this.rows = rows});
  }

}
