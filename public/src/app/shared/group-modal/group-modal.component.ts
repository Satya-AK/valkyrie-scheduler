import {AfterViewInit, Component, ElementRef, Input, OnInit, ViewChild} from '@angular/core';
import {Group, GroupContextService} from "../group-context.service";

@Component({
  selector: 'app-group-modal',
  templateUrl: './group-modal.component.html',
  styleUrls: ['./group-modal.component.css']
})
export class GroupModalComponent  {

  @ViewChild('groupModal') private model: any;

  @Input() show: boolean;

  ngInitViewDone: boolean = false;

  groups: Group[] = null;

  error: string;

  constructor(private groupContextService: GroupContextService) {}

  selectedGroup: Group = this.groupContextService.getCurrentGroup();

  new_group_request = {
    group_name: null,
    group_email: null
  };

  modalOptions = {
    backdrop: "static",
    keyboard: false,
    ignoreBackdropClick: true
  };

  ngSelectOptions = {
    labelField: 'name',
    valueField: 'name',
    maxItems: 1,
    searchField: 'name'
  };

  ngDoCheck() {
    if (this.ngInitViewDone) {
      if(this.show) {
        this.model.show();
      } else {
        this.model.hide();
      }
    }
  }

  ngAfterViewInit (): void {
    this.fetchGroups();
    this.ngInitViewDone = true;
  }

  fetchGroups() {
    this.groupContextService
      .listGroups()
      .subscribe(data => {console.log(data); this.groups = data}, err => this.error = err);
  }

  createNUse(data) {
    var group = new Group(null ,data.group_name, data.group_email, "this is a description");
    this.groupContextService
      .createGroup(group)
      .subscribe(data => { this.groupContextService.setCurrentGroup(data) ;this.model.hide() },
        error => this.error = error);
  }


  setGroup() {
    if (this.selectedGroup) {
      this.groupContextService.setCurrentGroup(this.selectedGroup);
      this.groupContextService.showUiFlag = false;
    }
  }

  onGroupSelected(data) {
    this.selectedGroup = data.value;
  }

}
