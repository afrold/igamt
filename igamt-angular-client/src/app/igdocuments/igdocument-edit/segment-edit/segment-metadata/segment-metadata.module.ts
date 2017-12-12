import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SegmentMetadataComponent } from './segment-metadata.component';
import {SegmentMetaDataRouting} from "./segment-metadata-routing.module";


@NgModule({
  imports: [
    CommonModule,SegmentMetaDataRouting

],
  declarations: [SegmentMetadataComponent]
})
export class SegmentMetadataModule { }
