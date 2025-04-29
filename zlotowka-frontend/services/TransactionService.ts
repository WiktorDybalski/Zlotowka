"use client";

import { useAuth } from "@/components/providers/AuthProvider";
import sendToBackend, {
  getAuthHeader,
  sendToBackendWithoutReturningJson,
} from "@/lib/sendToBackend";
import { Currency } from "./CurrencyController";

export interface NewOneTimeTransactionReq {
  name: string;
  amount: number;
  currency: Currency;
  isIncome: boolean;
  date: string; // ISO date string (np. "2025-04-28")
  description: string;
}

export interface OneTimeTransaction extends NewOneTimeTransactionReq {
  transactionId: number;
  userId: number;
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
      `onetime-transaction/all/${userId}`,
      withAuthHeader,
      "Failed to fetch transactions"
    );
  }

  async function createNewTransaction(
    transaction: NewOneTimeTransactionReq
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
    createNewTransaction,
    deleteTransaction,
    editTransaction,
  };
}
