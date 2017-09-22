
import { Component, EventEmitter, Input, Output,OnInit} from '@angular/core';
import {NodeService} from './nodeservice';
import {TreeNode} from './treenode';

@Component({
    selector: 'igamt-treetable',
    templateUrl: './treetable.component.html'
})

export class TreetableComponent implements OnInit {

    files: TreeNode[];

    constructor(private nodeService: NodeService) {}

    ngOnInit() : void {
        this.nodeService.getFileSystem().then(files => this.files = files);
    }

}
