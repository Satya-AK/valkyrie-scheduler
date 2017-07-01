import { Component, OnInit } from '@angular/core';
import {InstanceService} from "../instance.service";
import {Instance} from "../instance";
import {ActivatedRoute} from "@angular/router";
import {AlertService} from "../../shared/alert-service.service";
import {Subject} from "rxjs/Subject";

@Component({
  selector: 'app-job-instance',
  templateUrl: './job-instance.component.html',
  styleUrls: ['./job-instance.component.css']
})
export class JobInstanceComponent implements OnInit {

  data: Instance[];
  jobId: string;
  dtTrigger: Subject<any> = new Subject();

  constructor(private instanceService: InstanceService,
              private activatedRoute: ActivatedRoute,
              private alertService: AlertService) {
    this.jobId = activatedRoute.snapshot.params["jobId"];
    this.instanceService.listJobInstance(this.jobId)
      .subscribe(x => {this.data = x; this.dtTrigger.next()}, err => {this.alertService.showErrorMessage(err)});
  }

  ngOnInit() {
    this.instanceService.listJobInstance(this.jobId)
      .map(x => {this.data = x; this.dtTrigger.next()}, err => this.alertService.showErrorMessage(err));
  }

  viewLogUrl(instanceId: string) {
    return "#/instance/job/log/"+ instanceId;
  }

  dtOptions: DataTables.Settings = {
    order: [1],
    autoWidth: false,
    pagingType: 'full_numbers',
    columns: [{orderable: true},
      {orderable: true},
      {orderable: true},
      {orderable: true},
      {orderable: true},
      {orderable: true},
      {orderable: true}]
  };

}
