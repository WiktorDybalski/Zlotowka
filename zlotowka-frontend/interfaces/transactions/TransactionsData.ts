export interface TransactionData {
  transactionId?: number,
  name: string;
  amount: number;
  currency: {
    currencyId: number;
    isoCode: string;
  };
  isIncome: boolean;
  description: string;
  frequency: 'Raz' | 'Codziennie' | 'Co tydzień' | 'Co miesiąc';
  date: string;
  startDate?: string;
  endDate?: string;
}