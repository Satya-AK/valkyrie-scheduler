import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpModule, JsonpModule } from '@angular/http';
import { AlertBannerComponent } from './alert-banner/alert-banner.component';
import {AlertService} from "./alert-service.service";
import { ModalModule } from 'ngx-bootstrap/modal';
import { GroupModalComponent } from './group-modal/group-modal.component';
import {FormsModule} from "@angular/forms";
import {NgSelectizeModule} from "ng-selectize";


@NgModule({
  imports: [
    CommonModule,
    HttpModule,
    JsonpModule,
    ModalModule.forRoot(),
    FormsModule,
    NgSelectizeModule
  ],
  declarations: [AlertBannerComponent, GroupModalComponent],
  exports: [AlertBannerComponent, GroupModalComponent],
  providers: [AlertService]
})
export class SharedModule { }
