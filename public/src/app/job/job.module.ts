import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { JobListComponent } from './job-list/job-list.component';
import { DataTablesModule } from 'angular-datatables';
import {JobService} from "./job.service";
import { JobEditComponent } from './job-edit/job-edit.component';
import {FormsModule} from "@angular/forms";
import {ModalModule} from "ngx-bootstrap";

@NgModule({
  imports: [
    CommonModule,
    DataTablesModule,
    FormsModule,
    ModalModule.forRoot(),
  ],
  declarations: [JobListComponent, JobEditComponent],
  providers: [JobService]
})
export class JobModule { }
