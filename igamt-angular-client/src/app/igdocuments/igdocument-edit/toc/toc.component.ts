import {Component, Input, ViewChildren} from "@angular/core";
import {WorkspaceService, Entity} from "../../../service/workspace/workspace.service";
import {TocService} from "./toc.service";
import {isNullOrUndefined} from "util";
import {TreeDragDropService} from "primeng/components/common/treedragdropservice";
import  {ViewChild} from '@angular/core';
import {UITreeNode, Tree} from "primeng/components/tree/tree";
import {TreeNode} from "primeng/components/common/treenode";
import {falseIfMissing} from "protractor/built/util";


@Component({
  selector : 'igamt-toc',
  templateUrl:"./toc.component.html",
  styleUrls:["./toc.component.css"]
})
export class TocComponent {
  // @ViewChild(Tree) toc :Tree;

  @ViewChildren("p-treeNode") treeNode :TreeNode[];




  _ig : any;

  treeData: any;
  parentSource:any;
  parentDest:any;

  constructor(private _ws : WorkspaceService, private  tocService:TocService){

  }

  @Input() set ig(ig){
    this._ig = ig;
  }


  ngOnInit() {

    this.ig = this._ws.getCurrent(Entity.IG);
    // this.toc.dragDropService.stopDrag = function (x) {
    //   console.log("HT");
    //   console.log(x);
    // };

    this.treeData = this.tocService.buildTreeFromIgDocument(this._ig);
    console.log(this.treeData);
    //this.toc.allowDrop = this.allow;
    // this.toc.draggableNodes = true;
    // this.toc.droppableNodes = true;
    // this.toc.onNodeDrop.subscribe(x => {
    //   console.log(x);
    // });
  }

  print =function (obj) {
    console.log("Printing Obj");
    console.log(obj);
  };

  getPath =function (node) {
    if(node.data.position){
      if(node.parent.data.referenceType=="root"){
        return node.data.position;
      }else{
        return this.getPath(node.parent)+"."+node.data.position;
      }
    }
  };




  onDragStart(event,node) {
    console.log(event);

    console.log("Drag Start");
  };
  onDragEnd(event, node) {
    console.log("DRAG END ")


  };
  onDrop(event) {
    console.log("Performed");
    console.log(event);
  };
  onDragEnter(event, node) {

  };
  onDragLeave(event, node) {
  };

  prevent(dragNode: TreeNode, dropNode: TreeNode, dragNodeScope: any) {
    console.log("Called");
    return false;
  };

  allow(dragNode: TreeNode, dropNode: TreeNode, dragNodeScope: any) {
    if(dropNode&&dropNode.parent&&dropNode.parent.data) {

      if (dragNode.data.type == 'profile') {
        return dropNode.parent.data.type == 'root';
      } else if (dragNode.data.type == 'section') {
        return dropNode.parent.data.type=='root'|| dropNode.parent.data.type=='section';

      }
    }else {
      return false;
    }
  };


}
