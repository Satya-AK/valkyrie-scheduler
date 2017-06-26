import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {JobService} from "../job.service";
import {Job} from "../job";
import {AlertService} from "../../shared/alert-service.service";
import {Subject} from "rxjs/Subject";
import {DataTableDirective} from "angular-datatables";


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

  @ViewChild("delConfirmationModal") private  modal: any;

  constructor(private jobService: JobService, private alertService: AlertService) { }

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
    this.fetchJobs();
  }

  /**
   * fetch jobs from service
   */
  fetchJobs() {
    this.jobService
      .getJobs()
      .subscribe(rows => { this.rows = rows; this.dtTrigger.next()},
                 error => this.alertService.showErrorMessage(error))
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
