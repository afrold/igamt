/**
 * Created by JyWoo on 12/4/17.
 */
import {Component, Input, Output, EventEmitter} from "@angular/core";
import {Http} from "@angular/http";
import {PrimeDialogAdapter} from "../prime-ng-adapters/prime-dialog-adapter";
import {SelectItem} from 'primeng/primeng';

@Component({
    selector : 'datatype-binding-picker',
    templateUrl : 'datatype-binding-picker.template.html'
})
export class DatatypeBindingPickerComponent extends PrimeDialogAdapter  {
    public datatypes : any = {};
    public selectedDatatype : string = "";
    options: SelectItem[];

    constructor(private $http : Http){
        super();
    }

    select(){
        this.dismissWithData(this.selectedDatatype);
    }

    onDialogOpen(){
        let ctrl = this;
        this.options = [];
        if(this.datatypes){
            for(let key of Object.keys(this.datatypes)){
                this.options.push({label:this.datatypes[key].label , value: key});
            }
        }
    }

    ngOnInit(){
        this.hook(this);
    }

    getScopeLabel(leaf) {
        if (leaf) {
            if (leaf.scope === 'HL7STANDARD') {
                return 'HL7';
            } else if (leaf.scope === 'USER') {
                return 'USR';
            } else if (leaf.scope === 'MASTER') {
                return 'MAS';
            } else if (leaf.scope === 'PRELOADED') {
                return 'PRL';
            } else if (leaf.scope === 'PHINVADS') {
                return 'PVS';
            } else {
                return "";
            }
        }
    }
    hasSameVersion(element) {
        if (element) return element.hl7Version;
        return null;
    }
}
