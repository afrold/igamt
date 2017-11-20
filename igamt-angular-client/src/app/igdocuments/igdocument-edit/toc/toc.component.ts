import {Component, Input} from "@angular/core";
import {WorkspaceService, Entity} from "../../../service/workspace/workspace.service";
import {TocService} from "./toc.service";
import {isNullOrUndefined} from "util";
import {TreeDragDropService} from "primeng/components/common/treedragdropservice";


@Component({
  selector : 'igamt-toc',
  templateUrl:"./toc.component.html",
  styleUrls:["./toc.component.css"]
})
export class TocComponent {

  _ig : any;

  treeData: any;
  parentSource:any;
  parentDest:any;
  toc:any;

  constructor(private _ws : WorkspaceService, private  tocService:TocService, private dnd:TreeDragDropService){

  }

  @Input() set ig(ig){
    this._ig = ig;
  }


  ngOnInit(){

    this.ig = this._ws.getCurrent(Entity.IG);
    console.log(this._ig);


    this.treeData=this.tocService.buildTreeFromIgDocument(this._ig);
    this.toc= document.querySelector("p-tree");

    //console.log(this.toc.prototype);


  }

  print =function (obj) {
    console.log("Printing Obj");
    console.log(obj);
  }

  getPath =function (node) {
    if(node.data.sectionPosition){
      if(!node.parent){
        return node.data.sectionPosition;
      }else{
        return this.getPath(node.parent)+"."+node.data.sectionPosition;
      }
    }


  }

  onDragStart(event, node) {
    this.parentSource=node.data;

    };
  onDragEnd(event, node) {
    console.log("Drag is Ended");

  };
  onDrop(event, node) {
    console.log("Drop is Ended ============");


  };
  onDragEnter(event, node) {

  };
  onDragLeave(event, node) {

    if(this.parentSource.type==='section'){
      return;
    }
    else if(this.parentSource.type==='profile'){
      if(node.parent){
        event.preventDefault();

        this.dnd.stopDrag(this.parentSource);

      }else{
        return;
      }
    }else {

      event.preventDefault();

      this.dnd.stopDrag(this.parentSource);


    }
  };



}
