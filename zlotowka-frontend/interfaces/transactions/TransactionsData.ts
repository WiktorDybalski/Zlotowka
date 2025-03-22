// TODO: frequency, currency interfaces
export interface TransactionData {
  name: string;
  date: string;
  frequency: string;
  type: 'income' | 'expense';
  amount: string;
  currency: string;
}