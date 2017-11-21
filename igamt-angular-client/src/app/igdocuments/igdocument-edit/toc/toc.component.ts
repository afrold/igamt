import {Component, Input} from "@angular/core";
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
  @ViewChild(Tree) toc :Tree;

  _ig : any;

  treeData: any;
  parentSource:any;
  parentDest:any;

  constructor(private _ws : WorkspaceService, private  tocService:TocService, private dnd:TreeDragDropService){

  }

  @Input() set ig(ig){
    this._ig = ig;
  }


  ngOnInit(){

    this.ig = this._ws.getCurrent(Entity.IG);
    console.log(this._ig);


    this.treeData=this.tocService.buildTreeFromIgDocument(this._ig);
    console.log(this.toc);

  }

  print =function (obj) {
    console.log("Printing Obj");
    console.log(obj);
  }

  getPath =function (node) {
    if(node.data.sectionPosition){
      if(node.parent){
        return node.data.sectionPosition;
      }else{
        return this.getPath(node.parent)+"."+node.data.sectionPosition;
      }
    }


  }

  onDragStart(event, node) {
    console.log("Start Drag");
    this.parentSource=node.data;

    };
  onDragEnd(event, node) {
    //console.log("Drag is Ended");

  };
  onDrop(event, node) {
   // console.log("Drop is Ended ============");


  };
  onDragEnter(event, node) {

  };
  onDragLeave(event, node) {


    if(this.parentSource.type==='section'&&(!node.parent||node.data.type=='section')){
      this.toc.allowDrop=this.allow;

      return ;
    }
    else if(this.parentSource.type==='profile'){
      if(node.parent){
        this.toc.allowDrop=this.prevent;

        event.preventDefault();

        this.dnd.stopDrag(this.parentSource);


      }else{
        this.toc.allowDrop=this.allow;

        return;
      }
    }else {
      this.toc.allowDrop=this.prevent;

      event.preventDefault();

      this.dnd.stopDrag(this.parentSource);


    }
  };

  prevent(dragNode: TreeNode, dropNode: TreeNode, dragNodeScope: any) {
    console.log("Called");
    return false;
  };

  allow(dragNode: TreeNode, dropNode: TreeNode, dragNodeScope: any) {
    console.log("allowed");
    return true;
  };


}
