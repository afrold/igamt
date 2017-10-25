/**
 * Created by hnt5 on 10/19/17.
 */
import {Containable} from "../../domain/traits/containable.trait";
import {ApiResponse} from "../../domain/api/api-response.domain";
import {Observable, Subject, Subscription} from "rxjs";
import {DataLink} from "../../domain/api/data-link.domain";



export abstract class DataContainer<T extends Containable> {

  private _content : T;
  private observable$ : Subject<T>;

  constructor(){
    this.observable$ = new Subject<T>();
  }

  set content(data : T){
    this._content = data;
    this.observable$.next(data);
  }

  get content(){
    return this._content;
  }

  bind(variable : T) : Subscription {
    variable = this.content;
    return this.observable$.subscribe((value) => {
      variable = value;
    });
  }

  abstract clear();
  abstract isSet() : boolean;

  abstract synchronize() : Observable<ApiResponse>;
  abstract delete() : Observable<ApiResponse>;

  abstract fromLink(link : DataLink) : Observable<ApiResponse>;

}
