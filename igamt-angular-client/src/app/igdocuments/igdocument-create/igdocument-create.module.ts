import {NgModule}     from '@angular/core';
import {CommonModule} from '@angular/common';


import {IgDocumentCreateRoutingModule} from "./igdocument-create-routing.module";
import {IgDocumentCreateComponent} from "./igdocument-create.component";

import {MatStepperModule} from '@angular/material/stepper';
import {MatInputModule} from '@angular/material'
@NgModule({
  imports: [
    CommonModule,
    IgDocumentCreateRoutingModule,
    MatStepperModule,
    MatInputModule


  ],
  declarations: [
    IgDocumentCreateComponent
  ],
  providers:[]
})
export class IgDocumentCreateModule {}
