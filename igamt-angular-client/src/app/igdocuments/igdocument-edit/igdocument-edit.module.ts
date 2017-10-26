import {NgModule}     from '@angular/core';
import {CommonModule} from '@angular/common';
import {IgDocumentEditComponent} from './igdocument-edit.component';
import {IgDocumentMetadataComponent} from './igdocument-metadata/igdocument-metadata.component';
import {SectionComponent} from './section/section.component';
import {IgDocumentEditRoutingModule} from './igdocument-edit-routing.module';
import {AccordionModule, ButtonModule, TabViewModule, GrowlModule} from 'primeng/primeng';
import {IgDocumentGuard} from "./igdocument-edit.guard";

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
		IgDocumentEditComponent, IgDocumentMetadataComponent, SectionComponent
	],
  providers : [
    IgDocumentGuard
  ]
})
export class IgDocumentEditModule {}
