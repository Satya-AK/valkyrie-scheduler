import { Component, OnInit } from '@angular/core';
import {Job} from "../job";

@Component({
  selector: 'app-job-new',
  templateUrl: './job-new.component.html',
  styleUrls: ['./job-new.component.css']
})
export class JobNewComponent implements OnInit {

  job: Job;

  constructor() {
  }

  ngOnInit() {
    this.job = new Job("", "", "", "");
  }

  debug(element) {
    console.log(element)
  }

}
