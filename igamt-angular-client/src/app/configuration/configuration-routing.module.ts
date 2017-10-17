import {NgModule}     from '@angular/core';
import {RouterModule} from '@angular/router'
import {Configuration} from './configuration';

@NgModule({
	imports: [
		RouterModule.forChild([
			{path:'',component: Configuration}
		])
	],
	exports: [
		RouterModule
	]
})
export class ConfigurationRoutingModule {}
