import { Injectable } from '@angular/core';
import { IAjax } from './ajax.service.interface';

@Injectable({
  providedIn: 'root'
})
export class AngularAjaxImplService implements IAjax {

  constructor() { }
}
