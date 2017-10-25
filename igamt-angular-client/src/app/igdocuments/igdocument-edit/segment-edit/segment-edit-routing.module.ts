/**
 * Created by hnt5 on 10/23/17.
 */
import {RouterModule} from "@angular/router";
import {NgModule} from "@angular/core";
import {SegmentEditComponent} from "./segment-edit.component";

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: '',
        component: SegmentEditComponent,
        children: [
          {
            path: '',
            children: [

            ]
          }
        ]
      }

    ])
  ],
  exports: [
    RouterModule
  ]
})
export class SegmentEditRoutingModule {}
