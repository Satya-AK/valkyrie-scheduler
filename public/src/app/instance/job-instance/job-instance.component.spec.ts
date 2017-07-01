import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { JobInstanceComponent } from './job-instance.component';

describe('JobInstanceComponent', () => {
  let component: JobInstanceComponent;
  let fixture: ComponentFixture<JobInstanceComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ JobInstanceComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(JobInstanceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });
});
