import { Injectable } from '@angular/core';
import {Http} from "@angular/http";
import {BaseApiService} from "./base-api-service";
import {Observable} from "rxjs/Observable";
import {Subject} from "rxjs/Subject";
import {AlertService} from "./alert-service.service";


@Injectable()
export class GroupContextService extends BaseApiService {

  private currentGroup: Group = null;

  public groupContextObservable = new Subject();

  showUiFlag: boolean = false;

  constructor(private http: Http, private alertService: AlertService) {
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
  listGroups(): Observable<Group[]> {
    return this.http.get("/groups")
      .map(x => x.json().map(y => Group.fromJson(y)))
      .catch(err => this.handleError(err))
  }


  /**
   * fetch group
   * @param groupId
   * @returns {Observable<R|T>}
   */
  fetchGroup(groupId: string): Observable<Group> {
    return this.http.get("/group/fetch/"+groupId)
      .map(x => Group.fromJson(x.json()))
      .catch(err => this.handleError(err))
  }


  /**
   * create Group
   * @param group
   */
  createGroup(group: Group) {
    return this.http
      .post("/group/create", group.json())
      .map(x => group)
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


/**
 * Group Model class
 */
export class Group {

  id: string;
  name: string;
  email: string;
  description: string;

  constructor(id: string, name: string, email: string, description: string) {
    this.id  = id;
    this.name = name;
    this.email = email;
    this.description = description;
  }

  json() {
    return {
      id: this.id,
      name: this.name,
      email: this.email,
      desc: this.description
    }
  }

  static fromJson(json: any): Group {
    return new Group(json.id, json.name, json.email, json.description)
  }

}
