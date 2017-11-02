import {Component, Input} from "@angular/core";
import {WorkspaceService, Entity} from "../../../service/workspace/workspace.service";

@Component({
  selector : 'igamt-toc',
  templateUrl:"./toc.component.html",
  styleUrls:["./toc.component.css"]
})
export class TocComponent {

  _ig : any;

  constructor(private _ws : WorkspaceService){

  }

  @Input() set ig(ig){
    this._ig = ig;
  }

  ngOnInit(){
    this.ig = this._ws.getCurrent(Entity.IG);
    console.log(this._ig);
  }

}
