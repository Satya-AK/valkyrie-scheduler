import { Component, OnInit } from '@angular/core';
import {GroupContextService} from "../group/group.service";

@Component({
  selector: 'app-mainbody',
  templateUrl: './mainbody.component.html',
  styleUrls: ['./mainbody.component.css']
})
export class MainbodyComponent implements OnInit {

  constructor(public groupContextService: GroupContextService) { }


  ngOnInit() {
  }


  flipModalShow()  {
    this.groupContextService.showUiFlag = ! this.groupContextService.showUiFlag;
  }

}
