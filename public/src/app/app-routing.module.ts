import { NgModule }              from '@angular/core';
import { RouterModule, Routes }  from '@angular/router';
import {JobListComponent} from "./job/job-list/job-list.component";
import {TriggerListComponent} from "./trigger/trigger-list/trigger-list.component";
import {JobNewComponent} from "./job/job-new/job-new.component";



const appRoutes: Routes = [
  { path: 'jobs', component: JobListComponent },
  { path: 'job/new', component: JobNewComponent },
  { path: 'triggers', component: TriggerListComponent },
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
