import { Component, Input, SimpleChanges } from '@angular/core';
import { AppService } from '../app.service';
import { CurrencyRate } from '../app.interface';
import { Chart, ChartConfiguration, registerables } from 'chart.js';
Chart.register(...registerables);


@Component({
  selector: 'app-chart',
  imports: [],
  templateUrl: './chart.component.html',
  styleUrl: './chart.component.scss',
  standalone: true, 
})
export class ChartComponent {
  @Input() currency: string = '';
  currencyHistory: CurrencyRate[] = [];

  public config: ChartConfiguration = {
    type: 'line',
    options: {
      responsive: true,
    },
    data: {
      labels: [1,2,3,4,5,7],
      datasets: [{
        label: this.currency,
        data: [65, 59, 80, 81, 56, 55, 40],
        fill: false,
        borderColor: 'rgb(75, 192, 192)',
        tension: 0.1,
      }]
    },
  };

  chart?: Chart;

  constructor(private appService: AppService) {}

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['currency']) {
      this.appService.getCurrencyHistory(this.currency).subscribe(data => {
        if (this.chart) this.chart.destroy();
        this.currencyHistory = data;
        this.config.data.labels = this.currencyHistory.map(item => item.date);
        this.config.data.datasets[0].data = this.currencyHistory.map(item => item.rate);
        this.config.data.datasets[0].label = this.currency;
        this.chart = new Chart('currencyChart',this.config);
      });
    }
  }
}
