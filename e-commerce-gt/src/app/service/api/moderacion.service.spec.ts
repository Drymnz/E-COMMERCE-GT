import { TestBed } from '@angular/core/testing';

import { ModeracionService } from './moderacion.service';

describe('ModeracionService', () => {
  let service: ModeracionService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ModeracionService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
