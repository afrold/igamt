import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule }    from '@angular/forms';
import { HttpModule }    from '@angular/http';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LocationStrategy, HashLocationStrategy } from '@angular/common';
import { AppRoutingModule } from './app-routing.module';

import { AppComponent } from './app.component';
import { HomeComponent } from './home/home.component';
import { AboutComponent } from './about/about.component';
import { DocumentationComponent } from './documentation/documentation.component'

import { TestplanService } from './service/testplan.service';
import {Workspace} from "./service/workspace/workspace.service";
import {AlertModule} from "ngx-bootstrap";
import {NotFoundComponent} from "./common/404/404.component";


@NgModule({
    declarations: [
        AppComponent,
        HomeComponent,
        AboutComponent,
        DocumentationComponent,
        NotFoundComponent
    ],
    imports: [
        AlertModule.forRoot(),
        BrowserModule,
        FormsModule,
        ReactiveFormsModule,
        AppRoutingModule,
        HttpModule,
        BrowserAnimationsModule,
    ],
    providers: [
        { provide: LocationStrategy, useClass: HashLocationStrategy },
        TestplanService,
        Workspace
    ],
    bootstrap: [AppComponent]
})
export class AppModule { }
