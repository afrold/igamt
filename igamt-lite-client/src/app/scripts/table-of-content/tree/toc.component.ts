import {Component, EventEmitter, Input, Output, OnInit, Inject} from '@angular/core';
import {TreeModule,TreeNode} from 'primeng/primeng';
import {NodeService} from './toc.service'
import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';


@Component({
    selector: 'igamt-toc',
    templateUrl: './toc.component.html'
})

@NgModule({
    declarations: [],
    schemas: [ CUSTOM_ELEMENTS_SCHEMA],
})
export class TreeComponent implements OnInit {

    treeData: TreeNode[];
    @Input() igdocument;


    constructor(private nodeService: NodeService) {}

    ngOnInit() {
        console.log("======ddddddddddd==============")
        console.log(this.igdocument);

        this.treeData= this.nodeService.getTreeData(this.igdocument);

}
}

