import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {DataTablesModule} from "angular-datatables";
import {JobInstanceComponent} from "./job-instance/job-instance.component";
import {InstanceService} from "./instance.service";
import { InstanceViewLogComponent } from './instance-view-log/instance-view-log.component';

@NgModule({
  imports: [
    CommonModule,
    DataTablesModule,
  ],
  declarations: [JobInstanceComponent, InstanceViewLogComponent],
  providers: [InstanceService]
})
export class InstanceModule { }
