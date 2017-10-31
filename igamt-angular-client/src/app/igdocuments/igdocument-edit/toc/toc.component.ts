import {Component, Input} from "@angular/core";
import {Workspace, Entity} from "../../../service/workspace/workspace.service";

@Component({
  selector : 'igamt-toc',
  templateUrl:"./toc.component.html",
  styleUrls:["./toc.component.css"]
})
export class TocComponent {

  _ig : any;

  constructor(private _ws : Workspace){

  }

  @Input() set ig(ig){
    this._ig = ig;
  }

  ngOnInit(){
    this.ig = this._ws.getCurrent(Entity.IG);
    console.log(this._ig);
  }

}
