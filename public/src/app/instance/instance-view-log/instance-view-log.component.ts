import { Component, OnInit } from '@angular/core';
import {AlertService} from "../../shared/alert-service.service";
import {InstanceService} from "../instance.service";
import {ActivatedRoute} from "@angular/router";
import {Job} from "../../job/job";
import {Instance} from "../instance";

@Component({
  selector: 'app-instance-view-log',
  templateUrl: './instance-view-log.component.html',
  styleUrls: ['./instance-view-log.component.css']
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
    this.instanceService
      .fetchLog(this.instanceId)
      .subscribe(x => {this.stdout = x.stdout; this.stderr = x.stderr}, x => this.alertService.showErrorMessage(x));
    this.instanceService.fetch(this.instanceId)
      .subscribe(x => { console.log(""); this.instance = x}, err => this.alertService.showErrorMessage(err))
  }


  setViewTab(tab: string) {
    this.activeTab = tab;
  }


}
