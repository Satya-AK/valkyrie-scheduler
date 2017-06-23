import { Component } from '@angular/core';
import {AlertService} from "../alert-service.service";

@Component({
  selector: 'app-alert-banner',
  templateUrl: './alert-banner.component.html',
  styleUrls: ['./alert-banner.component.css']
})
export class AlertBannerComponent {

  constructor(public alertService: AlertService) { }

  close() {
    this.alertService.clearMessage();
  }

}
