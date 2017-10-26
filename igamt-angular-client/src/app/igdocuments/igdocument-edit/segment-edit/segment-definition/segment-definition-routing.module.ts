import {NgModule}     from '@angular/core';
import {RouterModule} from '@angular/router'
import {SegmentDefinitionComponent} from "./segment-definition.component";
import {CoConstraintTableComponent} from "./coconstraint-table/coconstraint-table.component";

@NgModule({
	imports: [
		RouterModule.forChild([
			{
				path: '',
				component: SegmentDefinitionComponent,
				children: [
          { path: 'coconstraints', component : CoConstraintTableComponent },
				]
			}
		])
	],
	exports: [
		RouterModule
	]
})
export class SegmentDefinitionRouting {}
