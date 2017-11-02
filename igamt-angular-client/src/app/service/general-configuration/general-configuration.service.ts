/**
 * Created by hnt5 on 11/2/17.
 */
import {Injectable} from "@angular/core";
import {Http} from "@angular/http";
@Injectable()
export class GeneralConfigurationService {

  //TODO ADDING OTHER CONFIG DATA
  _usages : any;

  constructor(private http : Http){

    //TODO GETTING USAGES FROM API
    this._usages = [ { label : 'R', value : 'R' },{ label : 'RE', value : 'RE' },{ label : 'C', value : 'C' }, { label : 'X', value : 'O' }];


  }

  get usages(){
    return this._usages;
  }




}
