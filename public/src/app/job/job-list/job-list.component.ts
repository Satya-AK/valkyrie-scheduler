import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {JobService} from "../job.service";
import {Job} from "../job";
import {AlertService} from "../../shared/alert-service.service";
import {Subject} from "rxjs/Subject";
import {GroupContextService} from "../../shared/group-context.service";
import {DataTableDirective} from "angular-datatables";
import {Subscription} from "rxjs/Subscription";


@Component({
  selector: 'app-job-list',
  templateUrl: './job-list.component.html',
  styleUrls: ['./job-list.component.scss']
})
export class JobListComponent implements OnInit {

  rows: Job[] = [];
  selectedRows: Job[] = [];
  selectAllState: boolean = false;
  selectedJobName: string = null;
  dtTrigger: Subject<any> = new Subject();
  private groupContextSubscription: Subscription = null;

  @ViewChild("delConfirmationModal") private  modal: any;

  @ViewChild(DataTableDirective) dtElement: DataTableDirective;



  constructor(private jobService: JobService,
              private alertService: AlertService,
              private groupContextService: GroupContextService) { }

  dtOptions: DataTables.Settings = {
    order: [2],
    autoWidth: false,
    pagingType: 'full_numbers',
    columns: [{ orderable: false, width: "3px"},
      { orderable: true},
      {orderable: true},
      {orderable: false, width: "3px"},
      {orderable: false, width: "3px"}]
  };


  jobEditUrl(jobName: string) {
    return "#/job/edit/"+jobName;
  }


  ngOnInit(): void {
    this.groupContextSubscription = this.groupContextService.groupContextObservable
      .subscribe(x => this.jobService.getJobs().subscribe(x => this.refreshData(x)));
    this.jobService.getJobs()
      .subscribe(rows => { this.rows = rows; this.dtTrigger.next()},
        error => this.alertService.showErrorMessage(error))
  }


  refreshData(data: Job[]) {
    console.log("refreshing data");
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
   * @param jobName
   */
  confirmDeleteJob(jobName) {
    this.selectedJobName = this.rows.filter(x => x.name == jobName)[0].name;
    this.modal.show();
  }

  /**
   * delete job
   * @param jobName
   */
  deleteJob(jobName) {
    this.jobService.deleteJobByName(jobName)
      .subscribe(x => {
        this.alertService.showSuccessMessage(x);
        this.rows = this.rows.filter(x => !(x.name == jobName));
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
