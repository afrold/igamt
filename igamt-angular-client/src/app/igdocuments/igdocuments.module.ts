import {NgModule}     from '@angular/core';
import {CommonModule} from '@angular/common';
import { FormsModule, ReactiveFormsModule }    from '@angular/forms';
import { Igdocuments } from './igdocuments';
import {IgdocumentsRoutingModule} from './igdocuments-routing.module';

import {TabMenuModule} from 'primeng/primeng';


@NgModule({
	imports: [
		CommonModule,
		FormsModule,
		TabMenuModule,
		IgdocumentsRoutingModule
	],
	declarations: [
		Igdocuments
	]
})
export class IgdocumentsModule {}
