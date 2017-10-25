/**
 * Created by hnt5 on 10/23/17.
 */
import {Component} from "@angular/core";


@Component({
  selector : 'segment-edit',
  templateUrl : './segment-edit.component.html'
})
export class SegmentEditComponent {

  segmentEditTabs : any[];

  constructor() {}

  ngOnInit(){
    this.segmentEditTabs = [
      {label: 'Metadata', icon: 'fa-list', routerLink:'./segment-metadata'},
      {label: 'Definition', icon: 'fa-list', routerLink:'./segment-definition'}
    ];
  }

}
