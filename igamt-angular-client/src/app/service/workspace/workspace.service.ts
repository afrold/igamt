import {Injectable} from "@angular/core";
import {Http} from "@angular/http";
import {BehaviorSubject} from "rxjs/BehaviorSubject";
/**
 * Created by hnt5 on 10/25/17.
 */
import {Md5} from 'ts-md5/dist/md5';

export class Entity {
  public static  IG: string ="IG";
  public static  SEGMENT: string= "SEGMENT";
  public static  DATATYPE :string= "DATATYPE";

  /* ADD ENTITY TYPE TO SUPPORT HERE */
}

@Injectable()
export class WorkspaceService {
  private currentHash;
  private currentObj;

  private map : { [index : string] : BehaviorSubject<any> };

  constructor(private http : Http){
    this.map={}
  }



  getCurrent(key : string){
    return this.map[key];
  }

  setCurrent(key : string, obj : any){
    let str=JSON.stringify(obj);
    this.currentHash=Md5.hashStr(str);
    if(this.map[key]){
      this.map[key].next(obj);
    }else{
      let elm=new BehaviorSubject<any>(obj);
      this.map[key] = elm;
    }


  }


  getPreviousHash(){
    console.log(this.currentHash);
    return this.currentHash;
  }


}
