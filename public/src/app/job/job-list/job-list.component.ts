import { Component, OnInit } from '@angular/core';
import {JobService} from "../job.service";
import {Job} from "../job";
import {AlertService} from "../../shared/alert-service.service";


@Component({
  selector: 'app-job-list',
  templateUrl: './job-list.component.html',
  styleUrls: ['./job-list.component.scss']
})
export class JobListComponent implements OnInit {

  rows: Job[] = [];

  constructor(private jobService: JobService, private alertService: AlertService) { }

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
      .subscribe(rows => this.rows = rows, error => this.alertService.showErrorMessage(error));
  }

}
