import { Component, EventEmitter, Input, Output,OnInit} from '@angular/core';
import {NodeService} from '../segment-structure/nodeservice';

@Component({
    selector: 'igamt-segment-metadata',
    templateUrl: './segmeta.component.html'
})

export class SegmetaComponent implements OnInit {

    @Input() segmentdata: any;

    constructor(private nodeService: NodeService) {}

    ngOnInit() : void {
        this.nodeService.getSegmentTreeNodes("segment001").then(segmentdata => {
            this.segmentdata = segmentdata;
        });
    }
}
