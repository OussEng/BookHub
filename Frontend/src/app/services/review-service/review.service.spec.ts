import { TestBed } from '@angular/core/testing';

import { ReviewServiceTs } from './review.service.ts';

describe('ReviewServiceTs', () => {
  let service: ReviewServiceTs;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ReviewServiceTs);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
