import { Component, OnInit } from '@angular/core';
import {GroupContextService} from "../group.service";
import {AlertService} from "../../shared/alert-service.service";
import {ActivatedRoute} from "@angular/router";
import {Group} from "../group";
import {UUID} from "angular2-uuid";

@Component({
  selector: 'app-group-edit',
  templateUrl: './group-edit.component.html',
  styleUrls: ['./group-edit.component.css']
})
export class GroupEditComponent implements OnInit {

  group: Group;

  private groupId: string;

  createMode: boolean = true;

  constructor(private groupService: GroupContextService,
              private alertService: AlertService,
              private activatedRoute: ActivatedRoute) {}


  ngOnInit() {
    this.groupId = this.activatedRoute.snapshot.params["groupId"];
    this.group = new Group(UUID.UUID().replace(/-/g,""),"", "", "");
    if (this.groupId) {
      this.createMode = false;
      this.groupService
        .fetch(this.groupId)
        .subscribe(x => this.group = x, err => this.alertService.showErrorMessage(err));
    }
  }

  createGroup() {
    this.groupService
      .create(this.group)
      .subscribe(data => {this.alertService.showSuccessMessage(data); window.location.href='#/groups';},
        data => this.alertService.showErrorMessage(data))
  }

  updateGroup() {
    this.groupService
      .update(this.group)
      .subscribe(data => {this.alertService.showSuccessMessage(data); window.location.href='#/groups'},
        data => this.alertService.showErrorMessage(data))
  }
}
