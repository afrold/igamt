import {NgModule}     from '@angular/core';
import {RouterModule} from '@angular/router'
import {IgdocumentsEdit} from './igdocuments-edit';
import {IgdocumentMetadataComponent} from './igdocument-metadata/igdocument-metadata.component';
import {SectionComponent} from './section/section.component';

@NgModule({
	imports: [
		RouterModule.forChild([
			{
				path: '',
				component: IgdocumentsEdit,
				children: [
					{
						path: '',
						children: [
							{ path: 'igdocument-metadata', component: IgdocumentMetadataComponent },
							{ path: 'section', component: SectionComponent },
							// { path: 'message', component: SharedIgsComponent },
							// { path: 'segment', component: AllIgsComponent },
							// { path: 'datatype', component: AllIgsComponent },
							// { path: 'valueset', component: AllIgsComponent },
							{ path: '', component: IgdocumentMetadataComponent }
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
export class IgdocumentsEditRoutingModule {}
