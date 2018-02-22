/**
 * Created by ena3 on 2/20/18.
 */
import {Injectable} from "@angular/core";
import {Resolve, ActivatedRouteSnapshot, RouterStateSnapshot} from "@angular/router";
import {Http} from "@angular/http";
import {Observable} from "rxjs";
@Injectable()
export class IgdocumentEditResolver implements Resolve<any>{

  constructor(private http : Http){

  }

  resolve(route : ActivatedRouteSnapshot , rstate : RouterStateSnapshot): Observable<any>{
    return this.http.get('api/igdocuments/'+route.params['id']).map(res => res.json());
  }
}
