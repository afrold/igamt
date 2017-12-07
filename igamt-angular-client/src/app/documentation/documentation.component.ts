import { Component, OnInit } from '@angular/core';
import { IndexedDbService } from '../service/indexed-db/indexed-db.service';

@Component({
  templateUrl: './documentation.component.html'
})

export class DocumentationComponent implements OnInit {
  constructor(private indexedDbService: IndexedDbService) {
    this.indexedDbService.init('588f2d4184ae56b0b8a41197');
  }

  ngOnInit() {
  }
  public getDatatype (datatypeId) {
    this.indexedDbService.getDatatype(datatypeId, function (datatype) {
      // console.log('datatype 579654555455fa34e848dcf7: ' + datatype.label);
      this.datatype = datatype;
    }.bind(this));
  }

  public saveDatatype (datatype) {
    this.indexedDbService.saveDatatype(datatype);
  }
}
