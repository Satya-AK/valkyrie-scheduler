import { Component, OnInit } from '@angular/core';
import {JobService} from "../job.service";
import {Job} from "../job";
import {AlertService} from "../../shared/alert-service.service";
import {Subject} from "rxjs/Subject";


@Component({
  selector: 'app-job-list',
  templateUrl: './job-list.component.html',
  styleUrls: ['./job-list.component.scss']
})
export class JobListComponent implements OnInit {

  rows: Job[] = [];
  selectedRows: Job[] = [];
  selectAllState: boolean = false;

  dtTrigger: Subject<any> = new Subject();


  constructor(private jobService: JobService, private alertService: AlertService) { }

  dtOptions: DataTables.Settings = {
    autoWidth: false,
    pagingType: 'full_numbers',
    columns: [{ orderable: false}, { orderable: true}, {orderable: true}, {orderable: false}]
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

    selectAll() {
       this.rows.forEach(x => x.checked = !this.selectAllState);
    }

}
