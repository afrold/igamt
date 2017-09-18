import { Component, EventEmitter, Input, Output,OnInit} from '@angular/core';

@Component({
  selector: 'igamt-footer',
  templateUrl: './footer.component.html'
})
export class FooterComponent implements OnInit {

  @Input() fullScreen: any;

  constructor() { }

  ngOnInit(): void {
  }
}

