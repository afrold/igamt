import { Component, EventEmitter, Input, Output,OnInit} from '@angular/core';
import {MessageNodeService} from './messageNodeService';
@Component({
    selector: 'igamt-message-structure',
    templateUrl: 'message.template.html'
})

export class  MessageTree implements OnInit {


    constructor(private messageNodeService: MessageNodeService) {

    }

    ngOnInit() : void {

    }
}
