import {Component, OnInit, ViewChild} from '@angular/core';
import {GroupContextService} from "../../shared/group-context.service";
import {AlertService} from "../../shared/alert-service.service";
import {Trigger} from "../trigger";
import {Subject} from "rxjs/Subject";
import {Subscription} from "rxjs/Subscription";
import {TriggerService} from "../trigger-service.service";
import {DataTableDirective} from "angular-datatables";

@Component({
  selector: 'app-trigger-list',
  templateUrl: './trigger-list.component.html',
  styleUrls: ['./trigger-list.component.css']
})
export class TriggerListComponent implements OnInit {


  constructor(private triggerService: TriggerService,
              private alertService: AlertService,
              private groupContextService: GroupContextService) { }

  rows: Trigger[] = [];
  selectedRows: Trigger[] = [];
  selectAllState: boolean = false;
  selectedTrigger: Trigger = null;
  dtTrigger: Subject<any> = new Subject();
  private groupContextSubscription: Subscription = null;

  @ViewChild("delConfirmationModal") private  modal: any;

  @ViewChild(DataTableDirective) dtElement: DataTableDirective;




  dtOptions: DataTables.Settings = {
    order: [2],
    autoWidth: false,
    pagingType: 'full_numbers',
    columns: [{ orderable: false, width: "3px"},
      {orderable: true},
      {orderable: true},
      {orderable: true},
      {orderable: false, width: "3px"},
      {orderable: false, width: "3px"}]
  };


  triggerEditUrl(id: string) {
    return "#/trigger/edit/"+id;
  }


  ngOnInit(): void {
    this.groupContextSubscription = this.groupContextService.groupContextObservable
      .subscribe(x => this.triggerService.list().subscribe(x => this.refreshData(x)));
    this.triggerService.list()
      .subscribe(rows => { this.rows = rows; this.dtTrigger.next()},
        error => this.alertService.showErrorMessage(error))
  }


  refreshData(data: Trigger[]) {
    this.dtElement.dtInstance.then((dtInstance: DataTables.Api) => {
      dtInstance.destroy();
      this.rows = data;
      this.dtTrigger.next();
    });
  }


  ngOnDestroy() {
    if (this.groupContextSubscription) {
      this.groupContextSubscription.unsubscribe();
    }
  }


  /**
   *
   * @param triggerId
   */
  confirmDeleteTrigger(triggerId: string) {
    this.selectedTrigger = this.rows.filter(x => x.id == triggerId)[0];
    this.modal.show();
  }

  /**
   * remove trigger
   * @param trigger
   */
  deleteTrigger(trigger: Trigger) {
    this.triggerService.remove(trigger)
      .subscribe(x => {
        this.alertService.showSuccessMessage(x);
        this.rows = this.rows.filter(x => !(x.id == trigger.id));
      }, x => this.alertService.showErrorMessage(x));
    this.modal.hide();
  }

  /**
   * select all jobs
   */
  selectAll() {
    this.rows.forEach(x => x.checked = !this.selectAllState);
  }

}
