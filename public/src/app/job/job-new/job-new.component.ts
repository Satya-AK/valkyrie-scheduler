import { Component, OnInit } from '@angular/core';
import {Job} from "../job";
import {JobService} from "../job.service";
import {AlertService} from "../../shared/alert-service.service";

@Component({
  selector: 'app-job-new',
  templateUrl: './job-new.component.html',
  styleUrls: ['./job-new.component.css']
})
export class JobNewComponent implements OnInit {

  job: Job;

  constructor(private jobService: JobService, private alertService: AlertService) {}

  ngOnInit() {
    this.job = new Job("", "", "", "");
  }

  createJob() {
    this.jobService
      .createJob(this.job)
      .subscribe(data => this.alertService.showSuccessMessage(data),
        data => this.alertService.showErrorMessage(data))
  }

}
