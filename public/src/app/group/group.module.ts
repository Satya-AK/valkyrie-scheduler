import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {DataTablesModule} from "angular-datatables";
import { GroupListComponent } from './group-list/group-list.component';
import {FormsModule} from "@angular/forms";
import {ModalModule} from "ngx-bootstrap";
import { GroupEditComponent } from './group-edit/group-edit.component';
import {GroupContextService} from "./group.service";

@NgModule({
  imports: [
    CommonModule,
    DataTablesModule,
    FormsModule,
    ModalModule.forRoot(),
  ],
  declarations: [GroupListComponent, GroupEditComponent],
  providers: [GroupContextService]
})
export class GroupModule { }
