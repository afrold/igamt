import {NgModule}     from '@angular/core';
import {CommonModule} from '@angular/common';
import {Search} from './search';
import {SearchRoutingModule} from './search-routing.module';
import {AccordionModule, ButtonModule, TabViewModule, GrowlModule} from 'primeng/primeng';

@NgModule({
	imports: [
		CommonModule,
		SearchRoutingModule,
        AccordionModule,
        ButtonModule,
        TabViewModule,
        GrowlModule
	],
	declarations: [
		Search
	]
})
export class SearchModule {}
