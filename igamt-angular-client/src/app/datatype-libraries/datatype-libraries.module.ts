import {NgModule}     from '@angular/core';
import {CommonModule} from '@angular/common';
import {DatatypeLibraries} from './datatype-libraries';
import {DatatypeLibrariesRoutingModule} from './datatype-libraries-routing.module';
import {AccordionModule, ButtonModule, TabViewModule, GrowlModule} from 'primeng/primeng';

@NgModule({
	imports: [
		CommonModule,
		DatatypeLibrariesRoutingModule,
        AccordionModule,
        ButtonModule,
        TabViewModule,
        GrowlModule
	],
	declarations: [
		DatatypeLibraries
	]
})
export class DatatypeLibrariesModule {}
