/**
 * Created by hnt5 on 10/1/17.
 */

import {Injectable} from "@angular/core";
import {Http} from "@angular/http";
import {TreeNode} from "primeng/components/common/treenode";

@Injectable()
export class SegmentTreeNodeService {

    constructor(private http: Http) {}

    getFD(segment){
        let nodes : TreeNode[] = [];
        let i = 1;
        for (let field of segment.fields){
            nodes.push(this.lazyNode(field,i++,null));
        }
        return nodes ;
    }

    lazyNode(element,i:number,parent){
        let node : TreeNode = {};
        node.label = element.name;
        node.data = {
            index : i,
            obj : element,
            path : (parent && parent.data && parent.data.path) ? parent.data.path+'.'+i : i+''
        };
        // node.parent = parent;
        node.type = element.type;
        node.leaf = !(element.datatype && element.datatype.name != 'ST');
        node.selectable = true;
        return node;
    }

    async getDT(node) {
        let nodes : TreeNode[] = [];
        let dummyDT = {
            components : [
                {
                    name : 'Component X',
                    type : 'component',
                    datatype : {
                        name : 'ST'
                    }
                }
            ]
        };

        let i = 1;
        for(let d of dummyDT.components){
            nodes.push(this.lazyNode(d,i++,node));
        }

        // await this.http.get('api/datatype/12345').map(data => data.json()).subscribe( data => {
        //
        // });

        return nodes;

    }
}