/**
 * Created by hnt5 on 10/19/17.
 */
import {Injectable} from "@angular/core";
import {DataContainer} from "./data-container.abstract.service";
import {Segment} from "../../domain/data/segment.domain";
import {Observable} from "rxjs";
import {ApiResponse} from "../../domain/api/api-response.domain";
import {DataLink} from "../../domain/api/data-link.domain";

@Injectable()
export class SegmentContainerService extends DataContainer<Segment> {

  clear(){
    this.content = null;
  }

  isSet(){
    return this.content !== null;
  }

  synchronize() : Observable<ApiResponse> {
    // Synchronize operation
    return new Observable<ApiResponse>( observer => observer.next());
  }

  delete() : Observable<ApiResponse>{
    // Delete operation
    this.clear();
    return new Observable<ApiResponse>( observer => observer.next());
  }

  fromLink(link : DataLink) : Observable<ApiResponse> {
    // Fetch form link
    return new Observable<ApiResponse>( observer => observer.next());
  }

}
