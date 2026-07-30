import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'loanStatus',
})
export class LoanStatusPipe implements PipeTransform {

  transform(value: string): string {
    switch (value) {
      case 'ACTIVE':
        return 'En cours';
      case 'RETURNED':
        return 'Rendu';
      case 'OVERDUE':
        return 'En retard';
      default:
        return value;
    }
  }

}
