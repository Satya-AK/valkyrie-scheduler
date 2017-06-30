import {Component, Input, OnInit} from '@angular/core';
import {Job} from "../job";
import {JobService} from "../job.service";
import {AlertService} from "../../shared/alert-service.service";
import {ActivatedRoute} from "@angular/router";
import {UUID} from "angular2-uuid";

@Component({
  selector: 'app-edit-new',
  templateUrl: './job-edit.component.html',
  styleUrls: ['./job-edit.component.css']
})
export class JobEditComponent implements OnInit {

  job: Job;

  private jobId: string;

  createMode: boolean = true;

  constructor(private jobService: JobService,
              private alertService: AlertService,
              private activatedRoute: ActivatedRoute) {}


  ngOnInit() {
    this.jobId = this.activatedRoute.snapshot.params["jobId"];
    this.job = new Job(UUID.UUID().replace(/-/g,""),"", "", "", "");
    if (this.jobId) {
      this.createMode = false;
      this.jobService
        .fetch(this.jobId)
        .subscribe(x => this.job = x, err => this.alertService.showErrorMessage(err));
    }
  }

  createJob() {
    this.jobService
      .create(this.job)
      .subscribe(data => {this.alertService.showSuccessMessage(data); window.location.href='#/jobs';},
        data => this.alertService.showErrorMessage(data))
  }

  updateJob() {
    this.jobService
      .update(this.job)
      .subscribe(data => {this.alertService.showSuccessMessage(data); window.location.href='#/jobs'},
        data => this.alertService.showErrorMessage(data))
  }

}
