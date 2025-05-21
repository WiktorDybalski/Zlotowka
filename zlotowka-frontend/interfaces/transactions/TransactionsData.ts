import { Currency } from "@/services/CurrencyController";

export interface TransactionData {
  transactionId?: number;
  name: string;
  amount: number;
  currency: {
    currencyId: number;
    isoCode: string;
  };
  isIncome: boolean;
  description: string;
  frequency: Period;
  date: string;
  startDate?: string;
  endDate?: string;
}

export interface NewOneTimeTransactionReq {
  name: string;
  amount: number;
  currency: Currency;
  isIncome: boolean;
  date: string; // ISO date string (np. "2025-04-28")
  description: string;
}

export interface NewRecurringTransactionReq {
  userId: number;
  name: string;
  amount: number;
  currencyId: number;
  isIncome: boolean;
  interval: string;
  firstPaymentDate: string;
  lastPaymentDate: string;
  description?: string;
}

export interface OneTimeTransaction extends NewOneTimeTransactionReq {
  transactionId: number;
  userId: number;
}

export interface PaginatedTransactionsResponse {
  transactions: DisplayedGeneralTransaction[];
  total: number;
  page: number;
  totalPages: number;
}

export interface EdittedOneTimeTransactionReq extends NewOneTimeTransactionReq {
  transactionId: number;
}

export interface Period {
  code: string;
  name: string;
}

export interface DisplayedGeneralTransaction {
  transactionId: number;
  userId: number;
  name: string;
  amount: number;
  currency: Currency;
  isIncome: boolean;
  date: string; // ISO date string
  description: string;
  period: "ONCE" | "RECURRING";
}

export type EdittedRecurringTransactionReq = NewRecurringTransactionReq;
