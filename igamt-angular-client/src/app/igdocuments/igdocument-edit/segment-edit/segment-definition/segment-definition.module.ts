import {NgModule, CUSTOM_ELEMENTS_SCHEMA}     from '@angular/core';
import {CommonModule} from '@angular/common';
import {GrowlModule} from 'primeng/primeng';
import {SegmentDefinitionRouting} from "./segment-definition-routing.module";
import {SegmentDefinitionComponent} from "./segment-definition.component";
import {FormsModule} from "@angular/forms";
import {TabMenuModule} from "primeng/components/tabmenu/tabmenu";
import {DialogModule} from "primeng/components/dialog/dialog";
import {DropdownModule} from "primeng/components/dropdown/dropdown";
import {SegmentTreeNodeService} from "../../../../common/segment-tree/segment-tree.service";
import {CoConstraintTableService} from "./coconstraint-table/coconstraint-table.service";
import {CCHeaderDialogUser} from "./coconstraint-table/header-dialog/header-dialog-user.component";
import {CCHeaderDialogDm} from "./coconstraint-table/header-dialog/header-dialog-dm.component";
import {SegmentTreeComponent} from "../../../../common/segment-tree/segment-tree.component";
import {CoConstraintTableComponent} from "./coconstraint-table/coconstraint-table.component";
import {TreeModule} from "primeng/components/tree/tree";
import {ValueSetBindingPickerComponent} from "../../../../common/valueset-binding-picker/valueset-binding-picker.component";
import {DataListModule} from "primeng/components/datalist/datalist";
import {DataTableModule} from "primeng/components/datatable/datatable";


@NgModule({
	imports: [
    CommonModule,
    FormsModule,
    TabMenuModule,
    DialogModule,
    DropdownModule,
    TreeModule,
    SegmentDefinitionRouting,
    GrowlModule,
    DataListModule,
    DataTableModule
	],
  providers : [CoConstraintTableService, SegmentTreeNodeService],
	declarations: [
    SegmentDefinitionComponent, CoConstraintTableComponent, SegmentTreeComponent, CCHeaderDialogDm, CCHeaderDialogUser, ValueSetBindingPickerComponent
	],
  schemas : [ CUSTOM_ELEMENTS_SCHEMA ]
})
export class SegmentDefinitionModule {}
