/**
 * Created by ena3 on 9/26/17.
 */
import {Injectable} from '@angular/core';
import {Http} from '@angular/http';

@Injectable()
export class DeltaNodeService {

    constructor(private http: Http) {}

    getDetaResult(left,right ) {
        return this.http.get('mocks/api/display/delta/' + left+ '/'+right+'.json')
            .toPromise()
            .then(res => <any> res.json());
    }
}