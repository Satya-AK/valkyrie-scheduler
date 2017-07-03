import {Component, OnInit, ViewChild} from '@angular/core';
import {AlertService} from "../../shared/alert-service.service";
import {InstanceService} from "../instance.service";
import {InstanceQuery} from "../instance-query";
import {Instance} from "app/instance/instance";
import {Subject} from "rxjs/Subject";
import {IMyDrpOptions} from "mydaterangepicker";
import {InstanceStatus} from "../instance-status";
import {FormControl} from "@angular/forms";
import {DataTableDirective} from "angular-datatables";

@Component({
  selector: 'app-instance-query',
  templateUrl: './instance-query.component.html',
  styleUrls: ['./instance-query.component.css']
})
export class InstanceQueryComponent implements OnInit {

  public rows: Instance[];
  public dtTrigger: Subject<any> = new Subject();
  public selectedDateRange: {beginDate: { year: number, month: number, day: number}, endDate: { year: number, month: number, day: number}} = null;
  public statusList: InstanceStatus[] = InstanceStatus.status;
  public selectedStatus: string = null;
  @ViewChild(DataTableDirective) dtElement: DataTableDirective;

  constructor(private alertService: AlertService,
              private instanceService: InstanceService) {
  }

  dtOptions: DataTables.Settings = {
    order: [1],
    autoWidth: false,
    pagingType: 'full_numbers',
    columns: [{orderable: true},
      {orderable: true},
      {orderable: true},
      {orderable: true},
      {orderable: true}]
  };

  ngSelectOptions = {
    labelField: 'name',
    valueField: 'id',
    searchField: 'name',
    maxItems: 1,
    allowEmptyOption: true,
  };

  private myDateRangePickerOptions: IMyDrpOptions = {
    dateFormat: 'yyyy-mm-dd',
    markCurrentDay: true,
    showClearBtn: false
  };

  ngOnInit() {
    this.setInitDate();
    this.instanceService
      .query(this.getInstanceQuery())
      .subscribe(x => {this.rows = x; this.dtTrigger.next()}, err => this.alertService.showErrorMessage(err))
  }

  private setInitDate() {
    let today = new Date();
    let yesterday = new Date();
    yesterday.setDate(today.getDate()-1);
    this.selectedDateRange = {beginDate: {year: yesterday.getFullYear(), month: yesterday.getMonth()+1, day: yesterday.getDate()},
      endDate: {year: today.getFullYear(), month: today.getMonth()+1, day: today.getDate()}};
  }

  fetchData() {
    this.instanceService
      .query(this.getInstanceQuery())
      .subscribe(x => {this.rows = x;this.refreshData()}, err => this.alertService.showErrorMessage(err))
  }

  private refreshData() {
    this.dtElement.dtInstance.then((dtInstance: DataTables.Api) => {
      dtInstance.destroy();
      this.dtTrigger.next();
    });
  }

  private getInstanceQuery() {
    let startDate = new Date(this.selectedDateRange.beginDate.year,
      this.selectedDateRange.beginDate.month-1, this.selectedDateRange.beginDate.day);
    let endDate = new Date(this.selectedDateRange.endDate.year,
      this.selectedDateRange.endDate.month-1, this.selectedDateRange.endDate.day);
    return new InstanceQuery(startDate.toISOString().substring(0,10),
      endDate.toISOString().substring(0,10),Number(this.selectedStatus))
  }

}
