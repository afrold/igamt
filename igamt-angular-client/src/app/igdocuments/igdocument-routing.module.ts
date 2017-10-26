import {NgModule}     from '@angular/core';
import {RouterModule} from '@angular/router';
import {IgDocumentComponent} from './igdocument.component';

@NgModule({
	imports: [
		RouterModule.forChild([
			{
				path: '',
				component: IgDocumentComponent,
				children: [
					{
						path: '',
						children: [
							{ path: 'igdocuments-list', loadChildren: './igdocument-list/igdocument-list.module#IgDocumentListModule' },
							{ path: 'igdocuments-edit', loadChildren: './igdocument-edit/igdocument-edit.module#IgDocumentEditModule' },
							{ path: '', loadChildren: './igdocument-list/igdocument-list.module#IgDocumentListModule'}
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
export class IgDocumentRoutingModule {}
