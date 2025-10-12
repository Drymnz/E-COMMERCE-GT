import { TestBed } from '@angular/core/testing';

import { ListConstantService } from './list-constant.service';

describe('ListConstantService', () => {
  let service: ListConstantService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ListConstantService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
