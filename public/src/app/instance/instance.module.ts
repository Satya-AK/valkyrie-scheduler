import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {DataTablesModule} from "angular-datatables";
import {JobInstanceComponent} from "./job-instance/job-instance.component";
import {InstanceService} from "./instance.service";
import { InstanceViewLogComponent } from './instance-view/instance-view.component';
import { InstanceQueryComponent } from './instance-query/instance-query.component';
import {MyDateRangePickerModule} from "mydaterangepicker";
import {FormsModule} from "@angular/forms";
import {NgSelectizeModule} from "ng-selectize";

@NgModule({
  imports: [
    CommonModule,
    NgSelectizeModule,
    DataTablesModule,
    MyDateRangePickerModule,
    FormsModule
  ],
  declarations: [JobInstanceComponent, InstanceViewLogComponent, InstanceQueryComponent],
  providers: [InstanceService]
})
export class InstanceModule { }
