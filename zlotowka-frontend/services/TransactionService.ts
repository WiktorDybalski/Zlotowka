"use client";

import { useAuth } from "@/components/providers/AuthProvider";
import sendToBackend, {
  getAuthHeader,
  sendToBackendWithoutReturningJson,
} from "@/lib/sendToBackend";
import {
  EdittedOneTimeTransactionReq,
  NewRecurringTransactionReq,
  OneTimeTransaction,
  PaginatedTransactionsResponse,
  Period,
  TransactionData
} from "@/interfaces/transactions/TransactionsData";


export function useTransactionService() {
  const { token, userId } = useAuth();

  if (!token) throw new Error("User Logged Out (Token not provided)!");

  const withAuthHeader = getAuthHeader(token);

  async function getPeriods(): Promise<Array<Period>> {
    return await sendToBackend(
        `period/all`,
        withAuthHeader,
        "Failed to fetch transactions"
    );
  }

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
      interval: transaction.frequency.code,
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
    getPeriods
  };
}
