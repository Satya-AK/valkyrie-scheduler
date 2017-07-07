import { NgModule }              from '@angular/core';
import { RouterModule, Routes }  from '@angular/router';
import {JobListComponent} from "./job/job-list/job-list.component";
import {TriggerListComponent} from "./trigger/trigger-list/trigger-list.component";
import {JobEditComponent} from "./job/job-edit/job-edit.component";
import {TriggerEditComponent} from "./trigger/trigger-edit/trigger-edit.component";
import {JobInstanceComponent} from "./instance/job-instance/job-instance.component";
import {InstanceViewLogComponent} from "./instance/instance-view/instance-view.component";
import {InstanceQueryComponent} from "./instance/instance-query/instance-query.component";
import {GroupListComponent} from "./group/group-list/group-list.component";
import {GroupEditComponent} from "./group/group-edit/group-edit.component";



const appRoutes: Routes = [
  { path: 'jobs', component: JobListComponent },
  { path: 'job/new', component: JobEditComponent },
  { path: 'job/edit/:jobId', component: JobEditComponent },
  { path: 'instance/job/list/:jobId', component: JobInstanceComponent},
  { path: 'instance/job/log/:instanceId', component: InstanceViewLogComponent },
  { path: 'instance', component: InstanceQueryComponent },
  { path: 'triggers', component: TriggerListComponent },
  { path: 'trigger/new', component: TriggerEditComponent },
  { path: 'trigger/edit/:triggerId', component: TriggerEditComponent },
  { path: 'groups', component: GroupListComponent },
  { path: 'group/new', component: GroupEditComponent },
  { path: 'group/edit/:groupId', component: GroupEditComponent },
  { path: '',   redirectTo: '/jobs', pathMatch: 'full' },
  { path: '**', component: JobListComponent }
];

@NgModule({
  imports: [
    RouterModule.forRoot(appRoutes, {useHash: true})
  ],
  exports: [
    RouterModule
  ]
})
export class AppRoutingModule {}
