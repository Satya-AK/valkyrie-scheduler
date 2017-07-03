import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { InstanceQueryComponent } from './instance-query.component';

describe('InstanceQueryComponent', () => {
  let component: InstanceQueryComponent;
  let fixture: ComponentFixture<InstanceQueryComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ InstanceQueryComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(InstanceQueryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
