import {NgModule}     from '@angular/core';
import {RouterModule} from '@angular/router'
import {SharedElements} from './shared-elements';

@NgModule({
	imports: [
		RouterModule.forChild([
			{path:'',component: SharedElements}
		])
	],
	exports: [
		RouterModule
	]
})
export class SharedElementsRoutingModule {}
