export interface NextTransactionResponse {
  date: string;
  amount: number;
  isIncome: boolean;
  currencyIsoCode: string;
  transactionName: string;
}
