import { Component, OnInit } from '@angular/core';
import {GroupContextService} from "../shared/group-context.service";

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit {

  constructor(public groupContextService: GroupContextService) { }

  ngOnInit() {
  }

  groups = this.groupContextService.listGroups()

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
