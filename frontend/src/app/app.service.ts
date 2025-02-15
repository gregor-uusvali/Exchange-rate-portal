import { Injectable } from '@angular/core';
import { Payload } from './app.interface';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class AppService {

  constructor(private http: HttpClient) { }

  getHome(): Observable<Payload> {
    return this.http.get<Payload>('http://localhost:8080/api');
  }
}
