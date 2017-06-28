import { Component, OnInit } from '@angular/core';
import {Trigger} from "../trigger";
import {TriggerService} from "../trigger-service.service";
import {AlertService} from "../../shared/alert-service.service";
import {ActivatedRoute} from "@angular/router";
import {Job} from "../../job/job";
import {JobService} from "../../job/job.service";

@Component({
  selector: 'app-trigger-edit',
  templateUrl: './trigger-edit.component.html',
  styleUrls: ['./trigger-edit.component.css']
})
export class TriggerEditComponent implements OnInit {

  trigger: Trigger;

  jobs: Job[];

  private triggerName: string;

  constructor(private jobService: JobService,
              private triggerService: TriggerService,
              private alertService: AlertService,
              private activatedRoute: ActivatedRoute) {
    this.jobService.getJobs().subscribe(x => this.jobs = x);
  }


  ngSelectOptions = {
    labelField: 'name',
    valueField: 'name',
    maxItems: 1,
    searchField: 'name'
  };

  ngOnInit() {
    this.triggerName = this.activatedRoute.snapshot.params["triggerName"];
    this.trigger = new Trigger("", "", "", "");
    if (this.triggerName) {
      this.triggerService
        .fetch(this.triggerName)
        .subscribe(x => this.trigger = x, err => this.alertService.showErrorMessage(err));
    }
  }

  /**
   * create trigger
   */
  createTrigger() {
    this.triggerService
      .create(this.trigger)
      .subscribe(data => {this.alertService.showSuccessMessage(data); window.location.href='#/triggers';},
        data => this.alertService.showErrorMessage(data))
  }


}
