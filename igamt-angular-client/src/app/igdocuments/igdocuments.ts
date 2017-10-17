import {Component} from '@angular/core';

@Component({
    templateUrl: './igdocuments.html'
})
export class Igdocuments {
    igDocumentsTabMenu: any[];

    ngOnInit() {
        this.igDocumentsTabMenu = [
            {label: 'IG Documents List', icon: 'fa-list', routerLink:'./igdocuments-list'},
            {label: 'Current IG Document', icon: 'fa-pencil-square-o', routerLink:'./igdocuments-edit'}
        ];
    }
}