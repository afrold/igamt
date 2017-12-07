/**
 * Created by hnt5 on 10/27/17.
 */
import {Component, Input, ViewChild, ElementRef} from "@angular/core";
import {SegmentTreeNodeService} from "../../../../../../common/segment-tree/segment-tree.service";
import {DatatypeBindingPickerComponent} from "../../../../../../common/datatype-binding-picker/datatype-binding-picker.component"
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
  @ViewChild(DatatypeBindingPickerComponent) dtPicker: DatatypeBindingPickerComponent;

  datatypeLib;
  usages : any[];
  tree;

  constructor(private treeNodeService : SegmentTreeNodeService, private configService : GeneralConfigurationService){}

  ngOnInit(){
    this.tree = this.treeNodeService.getFieldsAsTreeNodes(this.segment);
    this.usages = this.configService.usages;  
    this.treeNodeService.getDatatypeLibrary().subscribe(data => {
      this.datatypeLib = data;
    });
  }

  loadNode($event){
    if($event.node && !$event.node.children){
      return this.treeNodeService.getComponentsAsTreeNodes($event.node).then(nodes => $event.node.children = nodes);
    }
  }

  delLength(node){
    node.data.obj.minLength = 'NA';
    node.data.obj.maxLength = 'NA';
    node.data.obj.confLength = '';
  }

  delConfLength(node){
    node.data.obj.minLength = '';
    node.data.obj.maxLength = '';
    node.data.obj.confLength = 'NA';
  }

  openDTDialog(node) {
    this.dtPicker.open({
      datatypes: this.datatypeLib,
      selectedDatatype: node.data.obj.datatype.id
    }).subscribe(
      result => {
        if(result){
          node.data.obj.datatype.id = result;
          node.children = null;
          if(this.datatypeLib[node.data.obj.datatype.id].numOfChildren > 0)
            this.treeNodeService.getComponentsAsTreeNodes(node).then(nodes => node.children = nodes);
        }
      }
    );
  }
}
