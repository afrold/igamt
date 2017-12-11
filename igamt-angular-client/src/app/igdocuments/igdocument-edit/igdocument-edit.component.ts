import {Component, Input, OnInit} from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import {WorkspaceService, Entity} from "../../service/workspace/workspace.service";
import {Http} from "@angular/http";
import {MenuItem} from "primeng/components/common/menuitem";
import {IndexedDbService} from "../../service/indexed-db/indexed-db.service";

@Component({
    templateUrl: './igdocument-edit.component.html',
    styleUrls : ['./igdocument-edit.component.css']
})
export class IgDocumentEditComponent implements OnInit{

  items: any[];
  menui: any[];
  private _ig : any;
  test : any;


  constructor(private route : ActivatedRoute,
              private ws   : WorkspaceService,
              private dbService: IndexedDbService){
    console.log("Constructing");
    console.log(this.test);
    this.init();


    // this._ws.getCurrent(Entity.IG).subscribe(data=>{
    //   console.log("returning data");
    //   this._ig=data;
    //   this.dbService.init(this._ig);
    //
    // });

    console.log(this._ig);
  };

  init(){
    this.test="inside";

    let obs= this.ws.getCurrent(Entity.IG).subscribe(data=>{
      console.log(data);

      this._ig=data;

      this.dbService.init(this._ig);

    });
    if(!this._ig){
      console.log("nothing");
    }
  }

  ngOnInit(){




    this.items = [
      {
        label : "Close",
        icon  : "fa-times"
      },
      {
        label : "Verify",
        icon  : "fa-check"
      },
      {
        label : "Share",
        icon  : "fa-share-alt"
      },
      {
        label : "Usage Filter",
        model : [
          { label : "All Usages" },
          { label : "RE / C / O" }
        ]
      },
      {
        label : "Export",
        icon  : "fa-download"
      },
      {
        label : "Connect To GVT",
        icon  : "fa-paper-plane"
      }
    ];

    this.menui = [
      { label : "All Usages" },
      { label : "RE / C / O" }
    ];

  }

  printIg(){
    console.log(this._ig);

  }

}
