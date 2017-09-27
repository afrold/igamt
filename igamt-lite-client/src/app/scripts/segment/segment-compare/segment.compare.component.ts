/**
 * Created by ena3 on 9/26/17.
 */
import {Component, EventEmitter, Input, Output,OnInit} from '@angular/core';

import {DeltaNodeService} from './igamt.segment.compare.service';
@Component({
    selector: 'igamt-segment-compare',
    templateUrl: './igamt.segment.compare.html'
})

export class SegmentCompare implements OnInit {

    @Input() left: any;

    @Input() right  :any;

    ngOnInit(): void {

    }

    constructor(left: any, right:any) {

    }


}