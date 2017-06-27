import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TriggerListComponent } from './trigger-list/trigger-list.component';
import {ModalModule} from "ngx-bootstrap";
import {FormsModule} from "@angular/forms";
import {DataTablesModule} from "angular-datatables";

@NgModule({
  imports: [
    CommonModule,
    DataTablesModule,
    FormsModule,
    ModalModule.forRoot(),
  ],
  declarations: [TriggerListComponent]
})
export class TriggerModule { }
