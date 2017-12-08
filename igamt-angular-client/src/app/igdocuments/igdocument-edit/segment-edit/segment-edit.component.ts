/**
 * Created by hnt5 on 10/23/17.
 */
import {Component, Input} from "@angular/core";
import {ActivatedRoute, Router} from "@angular/router";
import {WorkspaceService, Entity} from "../../../service/workspace/workspace.service";


@Component({
  selector : 'segment-edit',
  templateUrl : './segment-edit.component.html',
  styleUrls : ['./segment-edit.component.css']
})
export class SegmentEditComponent {

  _segment;
  segmentEditTabs : any[];

  @Input() set segment(segment : any){
    this._segment = segment;
  }

  constructor(private _ws : WorkspaceService,    private route: ActivatedRoute,
              private router: Router){

    this.segment = _ws.getCurrent(Entity.SEGMENT);
    this.route.params.subscribe(x=>{

      console.log(x);
    });
  };

  ngOnInit(){
    this.segmentEditTabs = [
      {label: 'Metadata', icon: 'fa-info-circle', routerLink:'./metadata'},
      {label: 'Definition', icon: 'fa-table', routerLink:'./definition'},
      {label: 'Delta', icon: 'fa-table', routerLink:'./delta'},
      {label: 'Cross-Reference', icon: 'fa-link', routerLink:'./crossref'}
    ];
  }
  ngOnDestroy(){
    console.log( this.segment)
    console.log("test");
  }

}
