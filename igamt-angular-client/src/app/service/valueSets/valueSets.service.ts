import {Injectable} from '@angular/core';
import { Http} from '@angular/http';

@Injectable()
export class ValueSetsService {
  constructor(private http: Http) {}
  public getValueSets(igDocumentId, callback) {
    this.http.get('api/igdocuments/' + igDocumentId + '/valueSets').map(res => res.json()).subscribe(data => {
      callback(data);
    });
  }
}
