import { Component } from '@angular/core';

@Component({
    selector: 'app-sidebar',
    templateUrl: './sidebar.component.html',
    styleUrls: ['./sidebar.component.scss']
})
export class SidebarComponent {

    items = [
      {name: "Instances", path: "instance", icon: "fa fa-wrench fa-fw"},
      {name: "Jobs", path: "jobs", icon: "fa fa-wrench fa-fw"},
      {name: "Triggers", path: "triggers", icon: "fa fa-wrench fa-fw"},
      {name: "Groups", path: "groups", icon: "fa fa-wrench fa-fw"},
    ];
}
