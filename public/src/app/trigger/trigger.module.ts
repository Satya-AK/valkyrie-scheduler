import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TriggerListComponent } from './trigger-list/trigger-list.component';
import {ModalModule} from "ngx-bootstrap";
import {FormsModule} from "@angular/forms";
import {DataTablesModule} from "angular-datatables";
import { TriggerEditComponent } from './trigger-edit/trigger-edit.component';
import {NgSelectizeModule} from "ng-selectize";
import {TriggerService} from "./trigger-service.service";
import {JobService} from "../job/job.service";
import {JobModule} from "../job/job.module";

@NgModule({
  imports: [
    CommonModule,
    DataTablesModule,
    FormsModule,
    ModalModule.forRoot(),
    NgSelectizeModule,
    JobModule
  ],
  declarations: [TriggerListComponent, TriggerEditComponent],
  providers: [TriggerService, JobService]
})
export class TriggerModule { }
