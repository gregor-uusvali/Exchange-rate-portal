import {Component} from '@angular/core';
import {MatIconModule} from '@angular/material/icon';
import {MatButtonModule} from '@angular/material/button';
import {MatTableModule} from '@angular/material/table';
import { CurrencyRate } from './app.interface';
import { AppService } from './app.service';
import { DatePipe } from '@angular/common';
import { ChartComponent } from './chart/chart.component';
import { FormControl, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    DatePipe,
    ChartComponent,
    FormsModule, 
    MatFormFieldModule, 
    ReactiveFormsModule, 
    MatInputModule
  ],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss',
})
export class AppComponent {
  displayedColumns: string[] = ['currency', 'rate'];
  dataSource: CurrencyRate[] = [];
  datePipe = new DatePipe('en-US');
  currencyHistory: CurrencyRate[] = [];
  selectedCurrency: CurrencyRate | null = null;
  calculatorFC = new FormControl(null, [Validators.required, Validators.min(0)]);
  calculationObj: {input: number,currency: string, amount: number} | null = null;

  constructor(
    private appService: AppService,
  ) {
    
  }
  ngOnInit(): void {
    this.appService.getCurrentRates().subscribe((res) => {
      this.dataSource = res;
      this.selectCurrency(this.dataSource[0]);
    });
  }

  selectCurrency(currency: CurrencyRate) {
    if (this.selectedCurrency !== currency) {
      this.selectedCurrency = currency;
      this.calculatorFC.reset();
      this.calculationObj = null;
    }
  }

  calculateCurrencyToEur(event: Event) {
    event.preventDefault();
    console.log(this.calculatorFC.value);
    if (this.selectedCurrency && this.calculatorFC.value !== null) {
      const amount = (this.calculatorFC.value * this.selectedCurrency.rate).toFixed(4);
      this.calculationObj = {
        input: this.calculatorFC.value, 
        currency: this.selectedCurrency.currency, 
        amount: Number(amount)
      };
    }
  }
}
