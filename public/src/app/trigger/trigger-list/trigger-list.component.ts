import { Component, OnInit } from '@angular/core';
import {GroupContextService} from "../../shared/group-context.service";
import {AlertService} from "../../shared/alert-service.service";

@Component({
  selector: 'app-trigger-list',
  templateUrl: './trigger-list.component.html',
  styleUrls: ['./trigger-list.component.css']
})
export class TriggerListComponent implements OnInit {

  constructor(private groupContextService: GroupContextService,
              private alertService: AlertService) { }

  ngOnInit() {
  }

}
