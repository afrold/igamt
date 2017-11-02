/**
 * Created by hnt5 on 10/27/17.
 */
import {Component, Input, ElementRef} from "@angular/core";
import {SegmentTreeNodeService} from "../../../../../../common/segment-tree/segment-tree.service";
import {GeneralConfigurationService} from "../../../../../../service/general-configuration/general-configuration.service";
declare var jquery:any;
declare var $ :any;

@Component({
  selector : 'segment-table',
  templateUrl : './segment-table.component.html',
  styleUrls : ['./segment-table.component.css']
})
export class SegmentTableComponent {

  @Input() segment;
  usages : any[];
  tree;

  constructor(private treeNodeService : SegmentTreeNodeService, private configService : GeneralConfigurationService){}

  ngOnInit(){
    this.tree = this.treeNodeService.getFieldsAsTreeNodes(this.segment);
    this.usages = this.configService.usages;
  }

  loadNode($event){
    if($event.node){
      return this.treeNodeService.getComponentsAsTreeNodes($event.node).then(nodes => $event.node.children = nodes);
    }
  }
}
