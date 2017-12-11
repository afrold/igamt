import {Injectable} from "@angular/core";
import {Http} from "@angular/http";
import {Subject} from "rxjs";
import 'rxjs/add/observable/of';

import {Observable} from 'rxjs/Observable';

/**
 * Created by hnt5 on 10/25/17.
 */

export class Entity {
  public static  IG : string= "IG";
  public static SEGMENT : string ="SEGMENT";
 public static DATATYPE : string ="DATATYPE";
  /* ADD ENTITY TYPE TO SUPPORT HERE */
}

@Injectable()
export class WorkspaceService {


  private map : { [index : string] : Observable<any> } ={};


  constructor(private http : Http){
  }

  getObservable(key : string){
    return new Observable( observer =>
    observer.next(this.getCurrent(key))
    );


  }

  getCurrent(key : string){

    if(this.map[key]){
      return this.map[key];
    }else{
      return Observable.of(null);
    }
  }

  setCurrent(key : string, obj : any){

      this.map[key]=Observable.of(obj);

  }

}
