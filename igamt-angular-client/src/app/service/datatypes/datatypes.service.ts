import {Injectable} from '@angular/core';
import { Http} from '@angular/http';

@Injectable()
export class DatatypesService {
  constructor(private http: Http) {}
  public getDatatypes(igDocumentId, callback) {
    this.http.get('api/igdocuments/' + igDocumentId + '/datatypes').map(res => res.json()).subscribe(data => {
      callback(data);
    });
  }
}
