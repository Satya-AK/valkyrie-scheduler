import { Injectable } from '@angular/core';
import {Http} from "@angular/http";
import {GroupContextService} from "../shared/group-context.service";
import {Trigger} from "./trigger";
import {BaseApiService} from "../shared/base-api-service";

@Injectable()
export class TriggerServiceService extends BaseApiService {

  constructor(private http: Http,
              private groupContextService: GroupContextService) {
    super();
  }

  /**
   * list triggers
   */
  list() {
    this.http
      .get("/trigger/list/group/"+this.groupContextService.getCurrentGroup().name)
      .map(x => x.json().map(y => Trigger.fromJson(y)))
      .catch(err => this.handleError(err))
  }

}
