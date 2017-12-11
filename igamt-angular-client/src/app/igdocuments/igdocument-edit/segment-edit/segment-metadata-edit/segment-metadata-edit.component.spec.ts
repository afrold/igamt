import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SegmentMetadataEditComponent } from './segment-metadata-edit.component';

describe('SegmentMetadataEditComponent', () => {
  let component: SegmentMetadataEditComponent;
  let fixture: ComponentFixture<SegmentMetadataEditComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SegmentMetadataEditComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SegmentMetadataEditComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
