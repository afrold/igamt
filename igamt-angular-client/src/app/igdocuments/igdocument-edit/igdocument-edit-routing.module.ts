import {NgModule}     from '@angular/core';
import {RouterModule} from '@angular/router'
import {IgDocumentEditComponent} from './igdocument-edit.component';
import {IgDocumentMetadataComponent} from './igdocument-metadata/igdocument-metadata.component';
import {SectionComponent} from './section/section.component';
import {SegmentEditComponent} from "./segment-edit/segment-edit.component";

@NgModule({
	imports: [
		RouterModule.forChild([
			{
				path: '',
				component: IgDocumentEditComponent,
				children: [
					{
						path: '',
						children: [
							{ path: 'igdocument-metadata', component: IgDocumentMetadataComponent },
              { path: 'section', component: SectionComponent },
							{ path: 'segment', loadChildren: './segment-edit/segment-edit.module#SegmentEditModule' },
							// { path: 'message', component: SharedIgsComponent },
							// { path: 'segment', component: AllIgsComponent },
							// { path: 'datatype', component: AllIgsComponent },
							// { path: 'valueset', component: AllIgsComponent },
							{ path: '', component: IgDocumentMetadataComponent }
						]
					}
				]
			}
		])
	],
	exports: [
		RouterModule
	]
})
export class IgDocumentEditRoutingModule {}
