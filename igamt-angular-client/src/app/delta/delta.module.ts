import {NgModule}     from '@angular/core';
import {CommonModule} from '@angular/common';
import {Delta} from './delta';
import {DeltaRoutingModule} from './delta-routing.module';
import {AccordionModule, ButtonModule, TabViewModule, GrowlModule} from 'primeng/primeng';

@NgModule({
	imports: [
		CommonModule,
		DeltaRoutingModule,
        AccordionModule,
        ButtonModule,
        TabViewModule,
        GrowlModule
	],
	declarations: [
		Delta
	]
})
export class DeltaModule {}
