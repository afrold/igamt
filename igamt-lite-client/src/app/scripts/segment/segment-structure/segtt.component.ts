
import { Component, EventEmitter, Input, Output,OnInit} from '@angular/core';
import {NodeService} from './nodeservice';
import {TreeNode} from './treenode';
import {Config} from './config';

@Component({
    selector: 'igamt--segment-treetable',
    templateUrl: './segtt.component.html'
})

export class SegttComponent implements OnInit {

    segmentData: TreeNode[];

    config: Config;

    constructor(private nodeService: NodeService) {}

    ngOnInit() : void {
        this.nodeService.getSegmentTreeNodes("segment001").then(segmentData => {
                this.segmentData = segmentData;
        });

        this.nodeService.getConfig().then(config => {
            this.config = config;
        });
    }

    loadNode(event) {
        if(event.node) {
            //in a real application, make a call to a remote url to load children of the current node and add the new nodes as children
            this.nodeService.getDatatypeTreeNodes(event.node.data.datatype.id).then(nodes => event.node.children = nodes);
        }
    }

}
