import {Injectable} from '@angular/core';
import {Http, Response} from '@angular/http';
import {TreeNode} from './treenode';

@Injectable()
export class NodeService {

    constructor(private http: Http) {}

    getFileSystem() {
        return this.http.get('src/assets/testdata/filesystem.json')
            .toPromise()
            .then(res => <TreeNode[]> res.json().data);
    }
}