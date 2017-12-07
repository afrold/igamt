/**
 * Created by hnt5 on 10/23/17.
 */
import {RouterModule} from "@angular/router";
import {NgModule} from "@angular/core";
import {SegmentEditComponent} from "./segment-edit.component";
import {SegmentGuard} from "./segment-edit.guard";

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: ':id',
        component: SegmentEditComponent,
        canActivate : [ SegmentGuard ],
        children: [
          {
            path: 'definition',
            loadChildren: './segment-definition/segment-definition.module#SegmentDefinitionModule'
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
