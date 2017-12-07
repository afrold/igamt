/**
 * Created by hnt5 on 10/1/17.
 */

import {Injectable} from "@angular/core";
import {Http} from "@angular/http";
import {TreeNode} from "primeng/components/common/treenode";

@Injectable()
export class SegmentTreeNodeService {

  constructor(private http: Http) {
  }

  getFieldsAsTreeNodes(segment) {
    let nodes: TreeNode[] = [];
    let list = segment.fields.sort((x, y) => x.position - y.position);
    for (let field of list) {
      nodes.push(this.lazyNode(field, null, segment.valueSetBindings));
    }
    return nodes;
  }

  lazyNode(element, parent, bindings) {
    let node: TreeNode = {};

    node.label = element.name;
    node.data = {
      index: element.position,
      obj: element,
      path: (parent && parent.data && parent.data.path) ? parent.data.path + '.' + element.position : element.position + ''
    };

    this.getDatatypeLibrary().subscribe(datatypeLib => {
      if(datatypeLib && element.datatype && datatypeLib[element.datatype.id] && datatypeLib[element.datatype.id].numOfChildren && datatypeLib[element.datatype.id].numOfChildren > 0){
        node.leaf = false;
      }else {
        node.leaf = true;
      }
    });

    if(bindings){
      for(let binding of bindings){
        if(node.data.path === binding.location){
          node.data.binding = binding;
        }
      }
    }
  
    node.selectable = true;
    return node;
  }

  async getComponentsAsTreeNodes(node) {
    console.log(node.data.obj.datatype.id);

    let nodes: TreeNode[] = [];

    this.http.get('api/datatype/'+node.data.obj.datatype.id)
    .map(res => res.json()).subscribe(data => {
      console.log(data);
      for (let d of data.components) {
        nodes.push(this.lazyNode(d, node, data.valueSetBindings));
      }

    });
    return nodes;
  }

  getDatatypeLibrary() {
    return this.http.get('api/datatype').map(res => res.json());
  }
}
