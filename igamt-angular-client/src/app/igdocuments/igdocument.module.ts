import {NgModule}     from '@angular/core';
import {CommonModule} from '@angular/common';
import { FormsModule, ReactiveFormsModule }    from '@angular/forms';
import { IgDocumentComponent } from './igdocument.component';
import {IgDocumentRoutingModule} from './igdocument-routing.module';
import {TabMenuModule} from 'primeng/primeng';


@NgModule({
	imports: [
		CommonModule,
		FormsModule,
		TabMenuModule,
		IgDocumentRoutingModule
	],
	declarations: [
		IgDocumentComponent
	]
})
export class IgDocumentModule {}
