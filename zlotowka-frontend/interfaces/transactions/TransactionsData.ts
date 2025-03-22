export interface TransactionData {
  name: string;
  date: string;
  frequency: 'Raz' | 'Codziennie' | 'Co tydzień' | 'Co miesiąc';
  type: 'income' | 'expense';
  amount: string;
  currency: string;
}