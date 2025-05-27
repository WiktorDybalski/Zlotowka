"use client";

import { useAuth } from "@/components/providers/AuthProvider";
import sendToBackend, {
  getAuthHeader,
  sendToBackendWithoutReturningJson,
} from "@/lib/sendToBackend";
import {
  EdittedOneTimeTransactionReq,
  EdittedRecurringTransactionReq,
  NewRecurringTransactionReq,
  OneTimeTransaction,
  PaginatedTransactionsResponse,
  Period,
  RecurringTransaction,
  TransactionData,
} from "@/interfaces/transactions/TransactionsData";

export function useTransactionService() {
  const { token, userId } = useAuth();

  if (!token) throw new Error("User Logged Out (Token not provided)!");

  const withAuthHeader = getAuthHeader(token);

  async function getPeriods(): Promise<Array<Period>> {
    return await sendToBackend(
      `period/all`,
      withAuthHeader,
      "Nie udało się pobrać okresów dla transakcji"
    );
  }

  async function getTransactions(): Promise<PaginatedTransactionsResponse> {
    return await sendToBackend(
      `general-transactions/all/${userId}`,
      withAuthHeader,
      "Nie udało się pobrać transakcji"
    );
  }

  async function getTransactionsFromRange(
    startDate: string,
    endDate: string
  ): Promise<PaginatedTransactionsResponse> {
    return await sendToBackend(
      `general-transactions/all/${userId}?startDate=${startDate}&endDate=${endDate}&page=1&limit=1000`,
      withAuthHeader,
      "Nie udało się pobrać transakcji z podanego zakresu"
    );
  }

  async function getRecurringTransaction(
    transactionId: number
  ): Promise<RecurringTransaction> {
    return await sendToBackend(
      `recurring-transaction/${transactionId}`,
      withAuthHeader,
      "Nie udało się pobrać informacji o transakcji rekurencyjnej"
    );
  }

  async function createNewOneTimeTransaction(
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
      "Nie udało się dodać transakcji"
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

    return await sendToBackend(
      `recurring-transaction`,
      {
        ...withAuthHeader,
        method: "POST",
        body: JSON.stringify(recurringReq),
      },
      "Nie udało się dodać transakcji cyklicznej"
    );
  }

  async function deleteOneTimeTransaction(id: number): Promise<void> {
    await sendToBackendWithoutReturningJson(
      `onetime-transaction/${id}`,
      {
        ...withAuthHeader,
        method: "DELETE",
      },
      "Nie udało się usunąć transakcji"
    );
  }

  async function deleteRecurringTransaction(id: number): Promise<void> {
    await sendToBackendWithoutReturningJson(
      `recurring-transaction/${id}`,
      {
        ...withAuthHeader,
        method: "DELETE",
      },
      "Nie udało się usunąć transakcji cyklicznej"
    );
  }

  async function editOneTimeTransaction(
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
      "Nie udało się edytować transakcji"
    );
  }

  async function editRecurringTransaction(
    transaction: TransactionData
  ): Promise<EdittedRecurringTransactionReq> {
    const req: NewRecurringTransactionReq = {
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
    return await sendToBackend(
      `recurring-transaction/${transaction.transactionId}`,
      {
        ...withAuthHeader,
        method: "PUT",
        body: JSON.stringify(req),
      },
      "Nie udało się edytować transakcji cyklicznej"
    );
  }

  return {
    getTransactions,
    getTransactionsFromRange,
    createNewOneTimeTransaction,
    createNewRecurringTransaction,
    deleteOneTimeTransaction,
    deleteRecurringTransaction,
    editOneTimeTransaction,
    editRecurringTransaction,
    getPeriods,
    getRecurringTransaction,
  };
}
