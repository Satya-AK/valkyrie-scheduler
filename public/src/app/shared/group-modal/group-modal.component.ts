import {AfterViewInit, ChangeDetectorRef, Component, ElementRef, Input, OnInit, ViewChild} from '@angular/core';
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

  selectedGroup = this.groupContextService.getCurrentGroup()?this.groupContextService.getCurrentGroup().name:null;

  constructor(private groupContextService: GroupContextService) {}

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
      if(this.show && !this.model.isShown) {
        this.fetchGroups();
        this.model.show();
      } else if(!this.show && this.model.isShown) {
        this.model.hide();
      }
    }
  }

  ngAfterViewInit (): void {
    this.ngInitViewDone = true;
  }

  fetchGroups() {
    this.groupContextService
      .listGroups()
      .subscribe(data => {this.groups = data;
          if(this.groupContextService.getCurrentGroup()) {
             let groupItem = this.groups.filter(x => x.name == this.groupContextService.getCurrentGroup().name)[0];
            if(groupItem) {
               this.selectedGroup = groupItem.name;
            }
          }
        },
        err => this.error = err);
  }

  createNUse(data) {
    let group = new Group(null ,data.group_name, data.group_email, "this is a description");
    this.groupContextService
      .createGroup(group)
      .subscribe(data => { this.groupContextService.setCurrentGroup(data);
      this.selectedGroup = group.name;
      window.location.href='#/jobs';
      this.groupContextService.showUiFlag = false},
        error => this.error = error);
  }


  setGroup() {
    if (this.selectedGroup) {
      this.groupContextService.setCurrentGroup(this.groups.filter(x => x.name == this.selectedGroup)[0]);
      this.groupContextService.showUiFlag = false;
      window.location.href='#/jobs';
    }
  }

}
