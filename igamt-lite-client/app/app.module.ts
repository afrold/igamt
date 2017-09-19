import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { BrowserModule } from '@angular/platform-browser';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import { UpgradeModule } from '@angular/upgrade/static';
import { RouterModule} from '@angular/router';

import { AppComponent } from './app.component';
import { FooterComponent } from './scripts/footer/footer.component';

import { NodeService } from './scripts/table-of-content/tree/toc.service';
import { TreeComponent} from './scripts/table-of-content/tree/toc.component';


@NgModule({
  imports: [
    CommonModule,
    BrowserModule,
    BrowserAnimationsModule,
    UpgradeModule,
    RouterModule.forRoot([], { initialNavigation: false })
   ],
  providers: [
    NodeService
   ],
  declarations: [ AppComponent,FooterComponent ],
  bootstrap: [ AppComponent ]
})
export class AppModule {
  ngDoBootstrap() {


  }
}

