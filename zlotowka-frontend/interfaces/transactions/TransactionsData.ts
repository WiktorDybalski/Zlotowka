export interface TransactionData {
  name: string;
  date: string;
  frequency: "Raz" | "Codziennie" | "Co tydzień" | "Co miesiąc";
  isIncome: boolean;
  amount: number;
  currency: {
    currencyId: number,
    isoCode: string,
  },
  description: string;
}
