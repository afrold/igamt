import {NgModule}     from '@angular/core';
import {RouterModule} from '@angular/router';
import {Igdocuments} from './igdocuments';

@NgModule({
	imports: [
		RouterModule.forChild([
			{
				path: '',
				component: Igdocuments,
				children: [
					{
						path: '',
						children: [
							{ path: 'igdocuments-list', loadChildren: './igdocuments-list/igdocuments-list.module#IgdocumentsListModule' },
							{ path: 'igdocuments-edit', loadChildren: './igdocuments-edit/igdocuments-edit.module#IgdocumentsEditModule' },
							{ path: '', loadChildren: './igdocuments-list/igdocuments-list.module#IgdocumentsListModule'}
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
export class IgdocumentsRoutingModule {}