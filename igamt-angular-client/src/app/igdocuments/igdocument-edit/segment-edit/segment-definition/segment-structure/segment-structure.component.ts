/**
 * Created by hnt5 on 10/27/17.
 */
import {Component, Input} from "@angular/core";
import {WorkspaceService, Entity} from "../../../../../service/workspace/workspace.service";

@Component({
  selector : 'segment-structure',
  templateUrl : './segment-structure.component.html'
})
export class SegmentStructureComponent {

  _segment : any;

  constructor(private _ws : WorkspaceService){
    this.segment = _ws.getCurrent(Entity.SEGMENT);
  }

  @Input() set segment(s){
    this._segment = s;
  }

}
