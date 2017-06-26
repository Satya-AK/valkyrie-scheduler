import {Component, Input, OnInit} from '@angular/core';
import {Job} from "../job";
import {JobService} from "../job.service";
import {AlertService} from "../../shared/alert-service.service";
import {ActivatedRoute} from "@angular/router";

@Component({
  selector: 'app-edit-new',
  templateUrl: './job-edit.component.html',
  styleUrls: ['./job-edit.component.css']
})
export class JobEditComponent implements OnInit {

  job: Job;

  private jobName: string;

  constructor(private jobService: JobService,
              private alertService: AlertService,
              private activatedRoute: ActivatedRoute) {}


  ngOnInit() {
    this.jobName = this.activatedRoute.snapshot.params["jobName"];
    this.job = new Job("", "", "", "");
    if (this.jobName) {
      this.jobService
        .getJobByName(this.jobName)
        .subscribe(x => this.job = x, err => this.alertService.showErrorMessage(err));
    }
  }

  createJob() {
    this.jobService
      .createJob(this.job)
      .subscribe(data => {this.alertService.showSuccessMessage(data); window.location.href='#/jobs';},
        data => this.alertService.showErrorMessage(data))
  }

}
