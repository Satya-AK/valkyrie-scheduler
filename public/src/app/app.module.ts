import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AppComponent } from './app.component';
import { SidebarComponent } from './sidebar/sidebar.component';
import { HeaderComponent } from './header/header.component';
import { MainbodyComponent } from './mainbody/mainbody.component';
import {JobModule} from "./job/job.module";
import {TriggerModule} from "./trigger/trigger.module";
import {AppRoutingModule} from "./app-routing.module";
import { HttpModule } from '@angular/http';


@NgModule({
  declarations: [
    AppComponent,
    SidebarComponent,
    HeaderComponent,
    MainbodyComponent
  ],
  imports: [
    AppRoutingModule,
    JobModule,
    TriggerModule,
    BrowserModule,
    FormsModule,
    HttpModule,
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
