import {Injectable} from '@angular/core';
import {Http} from '@angular/http';
import {Config} from './config';

@Injectable()
export class NodeService {

    constructor(private http: Http) {}

    getSegmentTreeNodes(id) {
        return this.http.get('mocks/api/display/segment/' + id + '.json')
            .toPromise()
            .then(res => <any> res.json());
    }

    getDatatypeTreeNodes(id) {
        return this.http.get('mocks/api/display/datatype/' + id + '.json')
            .toPromise()
            .then(res => <any> res.json().structure);
    }

    getConfig() {
        return this.http.get('mocks/api/display/config.json')
            .toPromise()
            .then(res => <Config> res.json());
    }
}