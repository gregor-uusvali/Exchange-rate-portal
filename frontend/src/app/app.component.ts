import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { AppService } from './app.service';
import { Payload } from './app.interface';
import { FormatInputPathObject } from 'path';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent {
  homePayload: Payload | null = null;

  constructor(
    private appService: AppService
  ) {
    this.appService.getHome().subscribe((res) => this.homePayload = res);
  }

  updateExchangeRates() {
    this.appService.updateExchangeRates().subscribe((res) => this.homePayload = res);
  } 
}
