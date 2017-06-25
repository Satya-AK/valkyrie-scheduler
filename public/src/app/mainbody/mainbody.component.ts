import { Component, OnInit } from '@angular/core';
import {GroupContextService} from "../shared/group-context.service";

@Component({
  selector: 'app-mainbody',
  templateUrl: './mainbody.component.html',
  styleUrls: ['./mainbody.component.css']
})
export class MainbodyComponent implements OnInit {

  constructor(public groupContextService: GroupContextService) { }

  bingo = "Bingo!!!";

  ngOnInit() {
  }


  flipModalShow()  {
    this.groupContextService.showUiFlag = ! this.groupContextService.showUiFlag;
  }

}
