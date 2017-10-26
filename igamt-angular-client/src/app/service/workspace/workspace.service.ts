import {Injectable} from "@angular/core";
import {Http} from "@angular/http";
/**
 * Created by hnt5 on 10/25/17.
 */

export enum Entity {
  IG,
  SEGMENT,
  /* ADD ENTITY TYPE TO SUPPORT HERE */
}

@Injectable()
export class Workspace {

  private map : { [index : number] : any };

  constructor(private http : Http){
    this.map = {};
  }

  getCurrent(key : Entity){
    return this.map[key];
  }

  setCurrent(key : Entity, obj : any){
    this.map[key] = obj;
  }

}
