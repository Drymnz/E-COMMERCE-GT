import { TestBed } from '@angular/core/testing';

import { SancionService } from './sancion.service';

describe('SancionService', () => {
  let service: SancionService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SancionService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
