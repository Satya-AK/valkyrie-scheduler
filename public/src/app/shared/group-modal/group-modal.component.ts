import {AfterViewInit, Component, ElementRef, OnInit, ViewChild} from '@angular/core';

@Component({
  selector: 'app-group-modal',
  templateUrl: './group-modal.component.html',
  styleUrls: ['./group-modal.component.css']
})
export class GroupModalComponent implements AfterViewInit {

  @ViewChild('groupModal') model: any;

  selectedGroup: string = null;

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
    labelField: 'label',
    valueField: 'value',
    maxItems: 1,
    searchField: 'value'
  };

  ngAfterViewInit() {
    this.model.show();
  }

  groups = [{ value: "test_group_1", label: "test_group_1"},
    { value: "test_group_2", label: "test_group_2"},
    { value: "test_group_3", label: "test_group_3"},
    { value: "test_group_4", label: "test_group_4"}];

  submit() {
    if (this.selectedGroup) {
      this.model.hide();
    }
  }

  onGroupSelected(data) {
    this.selectedGroup = data.value;
  }

}
