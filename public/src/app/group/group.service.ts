import { Injectable } from '@angular/core';
import {Http} from "@angular/http";
import {Observable} from "rxjs/Observable";
import {Subject} from "rxjs/Subject";
import {BaseApiService} from "../shared/base-api-service";
import {Group} from "./group";


@Injectable()
export class GroupContextService extends BaseApiService {

  private currentGroup: Group = null;

  public groupContextObservable = new Subject();

  showUiFlag: boolean = false;

  constructor(private http: Http) {
    super();
    let groupData = localStorage.getItem("app-current-group");
    if(groupData) {
      try {
        this.currentGroup = Group.fromJson(JSON.parse(groupData));
      } catch (e) {
        this.showUiFlag = true;
        this.currentGroup = null;
      }
    } else {
      this.showUiFlag = true;
    }
  }

  /**
   * list groups
   */
  list(): Observable<Group[]> {
    return this.http.get("/groups")
      .map(x => x.json().map(y => Group.fromJson(y)))
      .catch(err => this.handleError(err))
  }


  /**
   * fetch group
   * @param groupId
   * @returns {Observable<R|T>}
   */
  fetch(groupId: string): Observable<Group> {
    return this.http.get("/group/fetch/"+groupId)
      .map(x => Group.fromJson(x.json()))
      .catch(err => this.handleError(err))
  }


  /**
   * create Group
   * @param group
   */
  create(group: Group) {
    return this.http
      .post("/group/create", group.json())
      .map(x => group)
      .catch(err => this.handleError(err))
  }

  /**
   * update group
   * @param group
   * @returns {Observable<R|T>}
   */
  update(group: Group) {
    return this.http
      .post("/group/update", group.json())
      .map(x => x.json().message)
      .catch(err => this.handleError(err))
  }

  /**
   * set group
   * @param group
   */
  setCurrentGroup(group: Group) {
    this.currentGroup = group;
    localStorage.setItem("app-current-group", JSON.stringify(group.json()));
    this.groupContextObservable.next(group);
  }


  getCurrentGroup() {
    return this.currentGroup;
  }


}
