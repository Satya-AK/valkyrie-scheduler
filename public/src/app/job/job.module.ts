import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { JobListComponent } from './job-list/job-list.component';
import { DataTablesModule } from 'angular-datatables';
import {JobService} from "./job.service";
import { JobNewComponent } from './job-new/job-new.component';
import {FormsModule} from "@angular/forms";

@NgModule({
  imports: [
    CommonModule,
    DataTablesModule,
    FormsModule
  ],
  declarations: [JobListComponent, JobNewComponent],
  providers: [JobService]
})
export class JobModule { }
