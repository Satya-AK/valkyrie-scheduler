import { NgModule }              from '@angular/core';
import { RouterModule, Routes }  from '@angular/router';
import {JobListComponent} from "./job/job-list/job-list.component";
import {TriggerListComponent} from "./trigger/trigger-list/trigger-list.component";
import {JobEditComponent} from "./job/job-edit/job-edit.component";
import {TriggerEditComponent} from "./trigger/trigger-edit/trigger-edit.component";



const appRoutes: Routes = [
  { path: 'jobs', component: JobListComponent },
  { path: 'job/new', component: JobEditComponent },
  { path: 'job/edit/:jobName', component: JobEditComponent },
  { path: 'triggers', component: TriggerListComponent },
  { path: 'trigger/new', component: TriggerEditComponent },
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
