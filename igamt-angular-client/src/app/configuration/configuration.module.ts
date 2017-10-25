import {NgModule}     from '@angular/core';
import {CommonModule} from '@angular/common';
import {ConfigurationComponent} from './configuration.compronent';
import {ConfigurationRoutingModule} from './configuration-routing.module';
import {AccordionModule, ButtonModule, TabViewModule, GrowlModule} from 'primeng/primeng';

@NgModule({
	imports: [
		CommonModule,
		ConfigurationRoutingModule,
        AccordionModule,
        ButtonModule,
        TabViewModule,
        GrowlModule
	],
	declarations: [
		ConfigurationComponent
	]
})
export class ConfigurationModule {}
