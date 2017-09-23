declare var angular: angular.IAngularStatic;
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { BrowserModule } from '@angular/platform-browser';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import { UpgradeModule } from '@angular/upgrade/static';
import { RouterModule} from '@angular/router';

import { TreeTableModule, SharedModule , DropdownModule} from 'primeng/primeng';

import { AppComponent } from './app.component';
import { FooterComponent } from './scripts/footer/footer.component';
import { SegttComponent } from './scripts/segment/segment-structure/segtt.component';
import { NodeService } from './scripts/segment/segment-structure/nodeservice'


import {HttpModule} from '@angular/http';


@NgModule({
  imports: [
    CommonModule,
    BrowserModule,
    BrowserAnimationsModule,
    UpgradeModule,
    RouterModule.forRoot([], { initialNavigation: false }),
    TreeTableModule,
    SharedModule,
    HttpModule,
    FormsModule,
    DropdownModule
   ],
  providers: [
    NodeService
   ],
  declarations: [ AppComponent, FooterComponent, SegttComponent],
  bootstrap: [ AppComponent ],
  entryComponents: [AppComponent, FooterComponent, SegttComponent]

})

export class AppModule {

  constructor(private upgrade: UpgradeModule) { }
  ngDoBootstrap() {}
}

