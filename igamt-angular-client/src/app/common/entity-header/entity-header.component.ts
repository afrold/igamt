/**
 * Created by hnt5 on 10/30/17.
 */
import {Component, Input} from "@angular/core";
import {Observable} from "rxjs";
@Component({
  selector : 'entity-header',
  templateUrl : './entity-header.component.html',
  styleUrls : ['./entity-header.component.css']
})
export class EntityHeaderComponent {
  @Input()
  _elm : any;

  constructor(){

  }

  @Input() set elm(e){
    this.elm = e;
  }

  header(){
    switch (this.elm.type){
      case 'segment' :
        return this.elm.label;
      case 'document' :
        return this.elm.metaData.title;
    }
    return 'ndef';
  }
  ngOnInit(){

  }
}
