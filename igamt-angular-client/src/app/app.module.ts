import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {HttpModule} from '@angular/http';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {LocationStrategy, HashLocationStrategy} from '@angular/common';

import {AppComponent} from './app.component';
import {HomeComponent} from './home/home.component';
import {AboutComponent} from './about/about.component';
import {DocumentationComponent} from './documentation/documentation.component';

import {AppInfoService} from './appinfo.service';
import {MenubarModule,PanelModule} from 'primeng/primeng';
import {AppRoutes} from './app.routes';
import {AppMenuComponent, AppSubMenuComponent} from './app.menu.component';
import {AppTopBarComponent} from './app.topbar.component';
import {AppFooterComponent} from './app.footer.component';
import {InlineProfileComponent} from './app.profile.component';

@NgModule({
  declarations: [
    AppComponent,
    HomeComponent,
    AboutComponent,
    DocumentationComponent,
    InlineProfileComponent,
    AppMenuComponent,
    AppSubMenuComponent,
    AppTopBarComponent,
    AppFooterComponent,
  ],
  imports: [
    BrowserModule,
    PanelModule,
    FormsModule,
    ReactiveFormsModule,
    AppRoutes,
    HttpModule,
    BrowserAnimationsModule,
    MenubarModule
  ],
  providers: [
    {provide: LocationStrategy, useClass: HashLocationStrategy},
     AppInfoService
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}
