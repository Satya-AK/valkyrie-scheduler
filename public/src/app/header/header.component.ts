import { Component, OnInit } from '@angular/core';
import {GroupContextService} from "../group/group.service";
import {Agent, OtherService} from "../shared/other.service";

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit {

  agents: Agent[] = [];

  constructor(public groupContextService: GroupContextService,
              private otherService: OtherService) { }

  ngOnInit() {
    this.otherService.agents().subscribe(x => { console.log(x) ;this.agents = x})
  }

  groups = this.groupContextService.list();

  getCurrentGroupName() {
    if (this.groupContextService.getCurrentGroup()) {
      return this.groupContextService.getCurrentGroup().name;
    }
    else {
      return "";
    }
  }

  show() {
    this.groupContextService.showUiFlag = true;
  }


}
