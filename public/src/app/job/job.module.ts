import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { JobListComponent } from './job-list/job-list.component';
import { DataTablesModule } from 'angular-datatables';
import {JobService} from "./job.service";
import { JobNewComponent } from './job-new/job-new.component';

@NgModule({
  imports: [
    CommonModule,
    DataTablesModule
  ],
  declarations: [JobListComponent, JobNewComponent],
  providers: [JobService]
})
export class JobModule { }
