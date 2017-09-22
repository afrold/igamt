declare var angular: angular.IAngularStatic;
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { BrowserModule } from '@angular/platform-browser';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import { UpgradeModule } from '@angular/upgrade/static';
import { RouterModule} from '@angular/router';

import { TreeTableModule, SharedModule } from 'primeng/primeng';


import { AppComponent } from './app.component';
import { FooterComponent } from './scripts/footer/footer.component';
import { JooterComponent } from './scripts/segment/treetable/jooter.component';
import { TreetableComponent } from './scripts/segment/treetable/treetable.component';
import { NodeService } from './scripts/segment/treetable/nodeservice'


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
    HttpModule
   ],
  providers: [
    NodeService
   ],
  declarations: [ AppComponent, FooterComponent, TreetableComponent, JooterComponent],
  bootstrap: [ AppComponent ],
  entryComponents: [AppComponent, FooterComponent, TreetableComponent, JooterComponent]

})

export class AppModule {

  constructor(private upgrade: UpgradeModule) { }
  ngDoBootstrap() {}
}

