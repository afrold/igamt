import {NgModule}     from '@angular/core';
import {CommonModule} from '@angular/common';


import {IgDocumentCreateRoutingModule} from "./igdocument-create-routing.module";
import {IgDocumentCreateComponent} from "./igdocument-create.component";

import {MatStepperModule} from '@angular/material/stepper';
import {MatInputModule} from '@angular/material'
import {IgDocumentCreateService} from "./igdocument-create.service";
import {TreeTableModule,SharedModule} from 'primeng/primeng';
import {MatRadioModule} from '@angular/material/radio';
import {FormsModule,ReactiveFormsModule} from "@angular/forms";
import {MatCheckboxModule} from '@angular/material/checkbox';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';

import {MatButtonModule} from '@angular/material/button';

@NgModule({
  imports: [
    TreeTableModule,SharedModule,
    CommonModule,
    IgDocumentCreateRoutingModule,
    MatStepperModule,
    MatInputModule,
    MatRadioModule,
    FormsModule,
    ReactiveFormsModule,
    MatCheckboxModule,
    MatProgressSpinnerModule,
    MatButtonModule

  ],
  declarations: [
    IgDocumentCreateComponent
  ],
  providers:[IgDocumentCreateService]
})
export class IgDocumentCreateModule {




}
