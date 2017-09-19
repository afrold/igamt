import {Component, EventEmitter, Input, Output, OnInit, Inject} from '@angular/core';
import {TreeModule,TreeNode} from 'primeng/primeng';
import {NodeService} from './toc.service'

@Component({
    selector: 'igamt-toc',
    templateUrl: './toc.component.html'
})

export class TreeComponent implements OnInit {

    treeData: TreeNode[];


    constructor(private nodeService: NodeService) {}

    ngOnInit() {
        console.log("====================");

        this.treeData= this.nodeService.getTreeData();
       console.log(this.treeData);

}
}

