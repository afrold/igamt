/**
 * Created by hnt5 on 10/26/17.
 */
import {Component, Input} from "@angular/core";
@Component({
  selector : 'display-label',
  templateUrl : './display-label.component.html',
  styleUrls : ['./display-label.component.css']
})
export class DisplayLabelComponent {
  _elm : any;

  constructor(){}
  ngOnInit(){}

  @Input() set elm(obj){
    this._elm = obj;
  }

  get elm(){
    return this._elm;
  }

  getScopeLabel() {
    if (this.elm) {
      if (this.elm.scope === 'HL7STANDARD') {
        return 'HL7';
      } else if (this.elm.scope === 'USER') {
        return 'USR';
      } else if (this.elm.scope === 'MASTER') {
        return 'MAS';
      } else if (this.elm.scope === 'PRELOADED') {
        return 'PRL';
      } else if (this.elm.scope === 'PHINVADS') {
        return 'PVS';
      } else {
        return "";
      }
    }
  }

  getVersion(){
    return this.elm ? this.elm.hl7Version : '';
  }
}
