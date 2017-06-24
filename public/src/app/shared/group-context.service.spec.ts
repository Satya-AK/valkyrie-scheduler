import { TestBed, inject } from '@angular/core/testing';

import { GroupContextService } from './group-context.service';

describe('GroupContextService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [GroupContextService]
    });
  });

  it('should be created', inject([GroupContextService], (service: GroupContextService) => {
    expect(service).toBeTruthy();
  }));
});
