import {Component, OnInit, ViewChild} from '@angular/core';
import {Group} from "../group";
import {Subject} from "rxjs/Subject";
import {Subscription} from "rxjs/Subscription";
import {DataTableDirective} from "angular-datatables";
import {AlertService} from "../../shared/alert-service.service";
import {GroupContextService} from "../group.service";

@Component({
  selector: 'app-group-list',
  templateUrl: './group-list.component.html',
  styleUrls: ['./group-list.component.css']
})
export class GroupListComponent implements OnInit {

  rows: Group[] = [];
  selectedRows: Group[] = [];
  selectAllState: boolean = false;
  selectedGroupId: string = null;
  dtTrigger: Subject<any> = new Subject();

  @ViewChild("delConfirmationModal") private  modal: any;
  @ViewChild(DataTableDirective) dtElement: DataTableDirective;



  constructor(private groupService: GroupContextService,
              private alertService: AlertService) { }

  dtOptions: DataTables.Settings = {
    order: [3],
    autoWidth: false,
    pagingType: 'full_numbers',
    columns: [{ orderable: false, width: "3px"},
      {orderable: true},
      {orderable: true},
      {orderable: false, width: "3px"},
      {orderable: false, width: "3px"},
      {orderable: false, width: "3px"}]
  };


  groupEditUrl(jobId: string) {
    return "#/group/edit/"+jobId;
  }


  ngOnInit(): void {
    this.groupService.list()
      .subscribe(rows => { this.rows = rows; this.dtTrigger.next()},
        error => this.alertService.showErrorMessage(error))
  }


  refreshData(data: Group[]) {
    this.dtElement.dtInstance.then((dtInstance: DataTables.Api) => {
      dtInstance.destroy();
      this.rows = data;
      this.dtTrigger.next();
    });
  }


  /**
   *
   * @param groupId
   */
  confirmDeleteGroup(groupId: string) {
    this.selectedGroupId = this.rows.filter(x => x.id == groupId)[0].id;
    this.modal.show();
  }

  /**
   * delete group
   * @param groupId
   */
  deleteGroup(groupId: string) {
    this.modal.hide();
  }

  /**
   * select all jobs
   */
  selectAll() {
    this.rows.forEach(x => x.checked = !this.selectAllState);
  }

}
