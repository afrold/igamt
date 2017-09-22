import { TestBed } from '@angular/core/testing';
import { TreetableComponent } from './treetable.component';

describe('App', () => {

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [TreetableComponent],
      providers: []
    });
  });

  it ('should work', () => {
    let fixture = TestBed.createComponent(TreetableComponent);
    expect(fixture.componentInstance instanceof TreetableComponent).toBe(true, 'should create TreetableComponent');
  });
});
