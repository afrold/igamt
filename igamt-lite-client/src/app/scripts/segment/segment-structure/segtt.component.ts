import {Component, EventEmitter, Input, Output,OnInit} from '@angular/core';
import {NodeService} from './nodeservice';
import {Config} from './config';

@Component({
    selector: 'igamt-segment-structure',
    templateUrl: './segtt.component.html'
})

export class SegttComponent implements OnInit {

    @Input() segmentdata: any;
    @Input() config: Config;

    constructor(private nodeService: NodeService) {}

    ngOnInit() : void {
        this.nodeService.getSegmentTreeNodes("segment001").then(segmentdata => {
            this.segmentdata = segmentdata;
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