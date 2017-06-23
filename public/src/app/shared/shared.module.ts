import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpModule, JsonpModule } from '@angular/http';
import { AlertBannerComponent } from './alert-banner/alert-banner.component';
import {AlertService} from "./alert-service.service";


@NgModule({
  imports: [
    CommonModule,
    HttpModule,
    JsonpModule,
  ],
  declarations: [AlertBannerComponent],
  exports: [AlertBannerComponent],
  providers: [AlertService]
})
export class SharedModule { }
