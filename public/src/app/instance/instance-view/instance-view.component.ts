import { Component, OnInit } from '@angular/core';
import {AlertService} from "../../shared/alert-service.service";
import {InstanceService} from "../instance.service";
import {ActivatedRoute} from "@angular/router";
import {Job} from "../../job/job";
import {Instance} from "../instance";

@Component({
  selector: 'app-instance-view',
  templateUrl: './instance-view.component.html',
  styleUrls: ['./instance-view.component.css']
})
export class InstanceViewLogComponent implements OnInit {

  instanceId: string = null;
  stderr: string = null;
  stdout: string = null;
  jobName: string = null;
  groupName: string = null;
  activeTab: string="info";
  instance: Instance = null;
  job: Job = null;

  constructor(private alertService: AlertService,
              private instanceService: InstanceService,
              private activatedRoute: ActivatedRoute) {
    this.instanceId = this.activatedRoute.snapshot.params['instanceId']
  }

  ngOnInit() {
    this.refreshData();
  }

  private refreshData() {
    this.instanceService
      .fetchLog(this.instanceId)
      .subscribe(x => {this.stdout = x.stdout; this.stderr = x.stderr}, x => this.alertService.showErrorMessage(x));
    this.instanceService.fetch(this.instanceId)
      .subscribe(x => { this.instance = x}, err => this.alertService.showErrorMessage(err))
  }

  setViewTab(tab: string) {
    this.activeTab = tab;
  }

  /**
   * kill instance
   */
  killInstance() {
    this.instanceService.kill(this.instanceId)
      .subscribe(x => {this.alertService.showSuccessMessage(x); this.refreshData()},
        err => this.alertService.showErrorMessage(err))
  }


  forceFinish() {
    this.instanceService.forceFinish(this.instanceId)
      .subscribe(x => { this.alertService.showSuccessMessage(x); this.refreshData() }
        , err => this.alertService.showErrorMessage(err))
  }


}
