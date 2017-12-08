import {Injectable} from "@angular/core";
import {Http} from "@angular/http";
import {Subject} from "rxjs";
/**
 * Created by hnt5 on 10/25/17.
 */

export enum Entity {
  IG,
  SEGMENT,
  DATATYPE
  /* ADD ENTITY TYPE TO SUPPORT HERE */
}

@Injectable()
export class WorkspaceService {


  private map : { [index : number] : any };

  constructor(private http : Http){
    this.map = {};
  }

  getObservable(key : Entity){

  }

  getCurrent(key : Entity){
    return this.map[key];
  }

  setCurrent(key : Entity, obj : any){
    this.map[key] = obj;
  }

}
