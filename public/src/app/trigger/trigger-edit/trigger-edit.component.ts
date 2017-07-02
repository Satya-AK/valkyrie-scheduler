import { Component, OnInit } from '@angular/core';
import {Trigger} from "../trigger";
import {TriggerService} from "../trigger-service.service";
import {AlertService} from "../../shared/alert-service.service";
import {ActivatedRoute} from "@angular/router";
import {Job} from "../../job/job";
import {JobService} from "../../job/job.service";
import {UUID} from "angular2-uuid";

@Component({
  selector: 'app-trigger-edit',
  templateUrl: './trigger-edit.component.html',
  styleUrls: ['./trigger-edit.component.css']
})
export class TriggerEditComponent implements OnInit {

  trigger: Trigger;

  jobs: Job[];

  private triggerId: string;

  constructor(private jobService: JobService,
              private triggerService: TriggerService,
              private alertService: AlertService,
              private activatedRoute: ActivatedRoute) {
    this.jobService.list().subscribe(x => this.jobs = x);
    this.trigger = new Trigger(UUID.UUID().replace(/-/g,""), "", "", "", "", false);
    this.triggerId = this.activatedRoute.snapshot.params["triggerId"];
    if (this.triggerId) {
      this.triggerService.fetch(this.triggerId)
        .subscribe(x => this.trigger, x => this.alertService.showErrorMessage(x))
    }
  }

  ngSelectOptions = {
    labelField: 'name',
    valueField: 'id',
    maxItems: 1,
    searchField: 'name'
  };

  ngOnInit() {
    this.triggerId = this.activatedRoute.snapshot.params["triggerId"];
    this.trigger = new Trigger(UUID.UUID().replace(/-/g,""),"", "", "", "", false);
    if (this.triggerId) {
      this.triggerService
        .fetch(this.triggerId)
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
