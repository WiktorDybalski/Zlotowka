"use client";

import { useAuth } from "@/components/providers/AuthProvider";
import sendToBackend, {
  getAuthHeader,
  sendToBackendWithoutReturningJson,
} from "@/lib/sendToBackend";
import { Currency } from "./CurrencyController";
import {TransactionData} from "@/interfaces/transactions/TransactionsData";
import {mapFrequencyToPeriodType} from "@/lib/utils";

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
  transactions: OneTimeTransaction[];
  total: number;
  page: number;
  totalPages: number;
}

export interface EdittedOneTimeTransactionReq extends NewOneTimeTransactionReq {
  transactionId: number;
}

export function useTransactionService() {
  const { token, userId } = useAuth();

  if (!token) throw new Error("User Logged Out (Token not provided)!");

  const withAuthHeader = getAuthHeader(token);

  async function getTransactions(): Promise<Array<OneTimeTransaction>> {
    return await sendToBackend(
      `general-transactions/all/${userId}`,
      withAuthHeader,
      "Failed to fetch transactions"
    );
  }

  async function getTransactionsFromRange(startDate: string, endDate: string): Promise<PaginatedTransactionsResponse> {
    return await sendToBackend(
        `general-transactions/all/${userId}?startDate=${startDate}&endDate=${endDate}&page=1&limit=1000`,
        withAuthHeader,
        "Failed to fetch transactions"
    );
  }

  async function createNewTransaction(
    transaction: TransactionData
  ): Promise<OneTimeTransaction> {
    const req = {
      userId: userId,
      name: transaction.name,
      amount: transaction.amount,
      currencyId: transaction.currency.currencyId,
      isIncome: transaction.isIncome,
      date: transaction.date,
      description: transaction.description,
    };

    console.log(req);
    return await sendToBackend(
      `onetime-transaction`,
      {
        ...withAuthHeader,
        method: "POST",
        body: JSON.stringify(req),
      },
      "Failed to create new transaction"
    );
  }

  async function createNewRecurringTransaction(
      transaction: TransactionData
  ): Promise<NewRecurringTransactionReq> {
    const recurringReq: NewRecurringTransactionReq = {
      userId: userId,
      name: transaction.name,
      amount: transaction.amount,
      currencyId: transaction.currency.currencyId,
      isIncome: transaction.isIncome,
      firstPaymentDate: transaction.startDate,
      lastPaymentDate: transaction.endDate,
      interval: mapFrequencyToPeriodType(transaction.frequency),
      description: transaction.description,
    };

    console.log(recurringReq);

    return await sendToBackend(
        `recurring-transaction`,
        {
          ...withAuthHeader,
          method: "POST",
          body: JSON.stringify(recurringReq),
        },
        "Failed to create new transaction"
    );
  }

  async function deleteTransaction(id: number): Promise<void> {
    await sendToBackendWithoutReturningJson(
      `onetime-transaction/${id}`,
      {
        ...withAuthHeader,
        method: "DELETE",
      },
      "Failed to delete transaction"
    );
  }

  async function editTransaction(
    transaction: EdittedOneTimeTransactionReq
  ): Promise<OneTimeTransaction> {
    const req = {
      userId: userId,
      name: transaction.name,
      amount: transaction.amount,
      currencyId: transaction.currency.currencyId,
      isIncome: transaction.isIncome,
      date: transaction.date,
      description: transaction.description,
    };
    return await sendToBackend(
      `onetime-transaction/${transaction.transactionId}`,
      {
        ...withAuthHeader,
        method: "PUT",
        body: JSON.stringify(req),
      },
      "Failed to update transaction"
    );
  }

  return {
    getTransactions,
    getTransactionsFromRange,
    createNewTransaction,
    createNewRecurringTransaction,
    deleteTransaction,
    editTransaction,
  };
}
