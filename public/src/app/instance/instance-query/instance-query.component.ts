import {Component, OnInit, ViewChild} from '@angular/core';
import {AlertService} from "../../shared/alert-service.service";
import {InstanceService} from "../instance.service";
import {InstanceQuery} from "../instance-query";
import {Instance} from "app/instance/instance";
import {Subject} from "rxjs/Subject";
import {IMyDrpOptions} from "mydaterangepicker";
import {InstanceStatus} from "../instance-status";
import {DataTableDirective} from "angular-datatables";
import {GroupContextService} from "../../group/group.service";
import {Subscription} from "rxjs/Subscription";

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
  private groupContextSubscription: Subscription = null;
  @ViewChild(DataTableDirective) dtElement: DataTableDirective;

  constructor(private alertService: AlertService,
              private groupContextService: GroupContextService,
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
    this.fetchData(false);
    this.groupContextSubscription = this.groupContextService.groupContextObservable
      .subscribe(x => this.fetchData(true), err => this.alertService.showErrorMessage(err))
  }

  ngOnDestroy() {
    if (this.groupContextSubscription) {
      this.groupContextSubscription.unsubscribe();
    }
  }

  private setInitDate() {
    let today = new Date();
    let yesterday = new Date();
    yesterday.setDate(today.getDate()-1);
    this.selectedDateRange = {beginDate: {year: yesterday.getFullYear(), month: yesterday.getMonth()+1, day: yesterday.getDate()},
      endDate: {year: today.getFullYear(), month: today.getMonth()+1, day: today.getDate()}};
  }

  fetchData(refreshDTInstance: boolean) {
    this.instanceService
      .query(this.getInstanceQuery())
      .subscribe(x => {if (refreshDTInstance) {this.refreshDTTable(x)} else { this.rows = x; this.dtTrigger.next()}},
        err => this.alertService.showErrorMessage(err))
  }

  private refreshDTTable(data: Instance[]) {
    this.dtElement.dtInstance.then((dtInstance: DataTables.Api) => {
      this.rows = data;
      dtInstance.destroy();
      this.dtTrigger.next();
    });
  }

  viewLogUrl(instanceId: string) {
    return "#/instance/job/log/"+ instanceId;
  }

  private getInstanceQuery() {
    let startDate = new Date(this.selectedDateRange.beginDate.year,
      this.selectedDateRange.beginDate.month-1, this.selectedDateRange.beginDate.day);
    let endDate = new Date(this.selectedDateRange.endDate.year,
      this.selectedDateRange.endDate.month-1, this.selectedDateRange.endDate.day);
    return new InstanceQuery(startDate.toISOString().substring(0,10),
      endDate.toISOString().substring(0,10),Number(this.selectedStatus), this.groupContextService.getCurrentGroup().id)
  }

}
