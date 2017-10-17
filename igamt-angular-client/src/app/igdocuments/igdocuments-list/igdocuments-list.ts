import {Component} from '@angular/core';

@Component({
    templateUrl: './igdocuments-list.html'
})
export class IgdocumentsList {
    igDocumentsListTabMenu: any[];

    ngOnInit() {
        this.igDocumentsListTabMenu = [
            {label: 'My IG Documents', icon: 'fa-file-text-o', routerLink:'./my-igs'},
            {label: 'Preloaded IG Documents', icon: 'fa-file-text-o', routerLink:'./preloaded-igs'},
            {label: 'Shared IG Documents', icon: 'fa-file-text-o', routerLink:'./shared-igs'},
            {label: 'All IG Documents', icon: 'fa-file-text-o', routerLink:'./all-igs'}
        ];
    }
}