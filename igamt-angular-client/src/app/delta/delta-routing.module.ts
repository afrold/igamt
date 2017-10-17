import {NgModule}     from '@angular/core';
import {RouterModule} from '@angular/router'
import {Delta} from './delta';

@NgModule({
	imports: [
		RouterModule.forChild([
			{path:'',component: Delta}
		])
	],
	exports: [
		RouterModule
	]
})
export class DeltaRoutingModule {}
