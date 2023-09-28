import { TestBed } from '@angular/core/testing';

import { AngularAjaxImplService } from './angular-ajax-impl.service';

describe('AngularAjaxImplService', () => {
  let service: AngularAjaxImplService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AngularAjaxImplService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
