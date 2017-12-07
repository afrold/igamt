import { Component, OnInit } from '@angular/core';
import {IgListService} from "../igdocument-list.service";

@Component({
  templateUrl: './my-igs.component.html'
})

export class MyIgsComponent implements OnInit {

  igs :any[];

  constructor(private listService :IgListService ) {

    listService.getListByType("USER").then( res =>
      this.igs= res);

  }

  ngOnInit() {

  }
}
