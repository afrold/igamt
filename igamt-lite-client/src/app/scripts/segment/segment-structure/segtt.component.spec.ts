import { TestBed } from '@angular/core/testing';
import { SegttComponent } from './segtt.component';

describe('App', () => {

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [SegttComponent],
      providers: []
    });
  });

  it ('should work', () => {
    let fixture = TestBed.createComponent(SegttComponent);
    expect(fixture.componentInstance instanceof SegttComponent).toBe(true, 'should create SegttComponent');
  });
});
