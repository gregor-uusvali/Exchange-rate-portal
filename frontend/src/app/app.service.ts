import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { CurrencyRate } from './app.interface';
@Injectable({
  providedIn: 'root'
})
export class AppService {
  apiUrl = 'http://localhost:8080/api/exchange-rates';

  constructor(private http: HttpClient) { }

  getCurrentRates(): Observable<CurrencyRate[]>{
    return this.http.get<CurrencyRate[]>(`${this.apiUrl}/current`);
  }

  getCurrencyHistory(type: string): Observable<CurrencyRate[]>{
    return this.http.get<CurrencyRate[]>(`${this.apiUrl}/currency/${type}`);
  }
}