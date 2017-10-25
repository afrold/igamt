import {NgModule}     from '@angular/core';
import {CommonModule} from '@angular/common';
import {IgDocumentListComponent} from './igdocument-list.component';
import {MyIgsComponent} from './my-igs/my-igs.component';
import {PreloadedIgsComponent} from './preloaded-igs/preloaded-igs.component';
import {SharedIgsComponent} from './shared-igs/shared-igs.component';
import {AllIgsComponent} from './all-igs/all-igs.component';
import {IgDocumentListRoutingModule} from './igdocument-list-routing.module';
import {TabMenuModule} from 'primeng/primeng';

@NgModule({
	imports: [
		CommonModule,
		IgDocumentListRoutingModule,
		TabMenuModule
	],
	declarations: [
		IgDocumentListComponent, MyIgsComponent, PreloadedIgsComponent, SharedIgsComponent, AllIgsComponent
	]
})
export class IgDocumentListModule {}
