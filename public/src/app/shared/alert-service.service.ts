import { Injectable } from '@angular/core';

@Injectable()
export class AlertService {

  constructor() { }

  public show: boolean = false;
  public message: string = null;
  public messageClass: string = null;


  showSuccessMessage(message: string) {
    this.messageClass = "alert alert-success alert-dismissable fade in";
    this.message = message;
    this.show = true;
  }

  showErrorMessage(message: string) {
    this.messageClass = "alert alert-danger alert-dismissable fade in";
    this.message = message;
    this.show = true;
  }

  clearMessage() {
    this.message = null;
    this.show = false;
  }


}
