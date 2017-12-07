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
      nodes.push(this.lazyNode(field, null));
    }
    return nodes;
  }

  lazyNode(element, parent) {
    let node: TreeNode = {};

    node.label = element.name;
    node.data = {
      index: element.position,
      obj: element,
      path: (parent && parent.data && parent.data.path) ? parent.data.path + '.' + element.position : element.position + ''
    };

    //TODO ADD METADATA TO CHECK IF LEAF
    node.leaf = !(element.datatype && element.datatype.name != 'ST');
    node.selectable = true;
    return node;
  }

  async getComponentsAsTreeNodes(node) {
    let nodes: TreeNode[] = [];

    //TODO GET FROM API
    let dummyDT = {
      components: [
        {
          name: 'Component X',
          type: 'component',
          position : 1,
          confLength: 'NA',
          datatype: {
            name: 'ST'
          }
        }
      ]
    };

    for (let d of dummyDT.components) {
      nodes.push(this.lazyNode(d, node));
    }

    return nodes;

  }
}
