import {NgModule}     from '@angular/core';
import {CommonModule} from '@angular/common';
import {IgdocumentsList} from './igdocuments-list';
import {MyIgsComponent} from './my-igs/my-igs.component';
import {PreloadedIgsComponent} from './preloaded-igs/preloaded-igs.component';
import {SharedIgsComponent} from './shared-igs/shared-igs.component';
import {AllIgsComponent} from './all-igs/all-igs.component';
import {IgdocumentsListRoutingModule} from './igdocuments-list-routing.module';
import {TabMenuModule} from 'primeng/primeng';

@NgModule({
	imports: [
		CommonModule,
		IgdocumentsListRoutingModule,
		TabMenuModule
	],
	declarations: [
		IgdocumentsList, MyIgsComponent, PreloadedIgsComponent, SharedIgsComponent, AllIgsComponent
	]
})
export class IgdocumentsListModule {}
