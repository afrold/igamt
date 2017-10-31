/**
 * Created by hnt5 on 10/27/17.
 */
import {Component, Input} from "@angular/core";
import {SegmentTreeNodeService} from "../../../../../../common/segment-tree/segment-tree.service";
@Component({
  selector : 'segment-table',
  templateUrl : './segment-table.component.html',
  styleUrls : ['./segment-table.component.css']
})
export class SegmentTableComponent {

  @Input() segment;
  usages : any[];
  tree;

  constructor(private treeNodeService : SegmentTreeNodeService){}
  ngOnInit(){
    this.tree = this.treeNodeService.getFD(this.segment);
    this.usages = [ { label : 'R', value : 'R' },{ label : 'RE', value : 'RE' },{ label : 'C', value : 'C' },
                    { label : 'X', value : 'O' }]
  }

}
