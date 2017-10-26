import {NgModule, CUSTOM_ELEMENTS_SCHEMA}     from '@angular/core';
import {CommonModule} from '@angular/common';
import {IgDocumentEditComponent} from './igdocument-edit.component';
import {IgDocumentMetadataComponent} from './igdocument-metadata/igdocument-metadata.component';
import {SectionComponent} from './section/section.component';
import {IgDocumentEditRoutingModule} from './igdocument-edit-routing.module';
import {AccordionModule, ButtonModule, TabViewModule, GrowlModule} from 'primeng/primeng';
import {IgDocumentGuard} from "./igdocument-edit.guard";
import {TocComponent} from "./toc/toc.component";

@NgModule({
	imports: [
		CommonModule,
		IgDocumentEditRoutingModule,
    AccordionModule,
    ButtonModule,
    TabViewModule,
    GrowlModule
	],
	declarations: [
		IgDocumentEditComponent, IgDocumentMetadataComponent, SectionComponent, TocComponent
	],
  providers : [
    IgDocumentGuard
  ],
  schemas : [ CUSTOM_ELEMENTS_SCHEMA ]
})
export class IgDocumentEditModule {}
