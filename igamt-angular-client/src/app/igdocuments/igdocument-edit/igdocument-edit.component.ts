import {Component} from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import {Workspace, Entity} from "../../service/workspace/workspace.service";
import {Http} from "@angular/http";

@Component({
    templateUrl: './igdocument-edit.component.html'
})
export class IgDocumentEditComponent {

  constructor(private route : ActivatedRoute,
              private _ws   : Workspace,
              private $http : Http){};

  ngOnInit(){

  }
}
