import {NgModule}     from '@angular/core';
import {RouterModule} from '@angular/router'
import {Search} from './search';

@NgModule({
	imports: [
		RouterModule.forChild([
			{path:'',component: Search}
		])
	],
	exports: [
		RouterModule
	]
})
export class SearchRoutingModule {}
