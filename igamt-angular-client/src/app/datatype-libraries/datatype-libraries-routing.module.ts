import {NgModule}     from '@angular/core';
import {RouterModule} from '@angular/router'
import {DatatypeLibraries} from './datatype-libraries';

@NgModule({
	imports: [
		RouterModule.forChild([
			{path:'',component: DatatypeLibraries}
		])
	],
	exports: [
		RouterModule
	]
})
export class DatatypeLibrariesRoutingModule {}
