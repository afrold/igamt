import {NgModule}     from '@angular/core';
import {CommonModule} from '@angular/common';
import {IgdocumentsEdit} from './igdocuments-edit';
import {IgdocumentMetadataComponent} from './igdocument-metadata/igdocument-metadata.component';
import {SectionComponent} from './section/section.component';
import {IgdocumentsEditRoutingModule} from './igdocuments-edit-routing.module';
import {AccordionModule, ButtonModule, TabViewModule, GrowlModule} from 'primeng/primeng';

@NgModule({
	imports: [
		CommonModule,
		IgdocumentsEditRoutingModule,
        AccordionModule,
        ButtonModule,
        TabViewModule,
        GrowlModule
	],
	declarations: [
		IgdocumentsEdit, IgdocumentMetadataComponent, SectionComponent
	]
})
export class IgdocumentsEditModule {}
