import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { InstanceViewLogComponent } from './instance-view-log.component';

describe('InstanceViewLogComponent', () => {
  let component: InstanceViewLogComponent;
  let fixture: ComponentFixture<InstanceViewLogComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ InstanceViewLogComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(InstanceViewLogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
