"use client";

import { useAuth } from "@/components/providers/AuthProvider";
import sendToBackend, { getAuthHeader } from "@/lib/sendToBackend";

export interface CurrentBalanceResponse {
  currentBalance: number;
}

export interface EstimatedBalanceResponse {
  estimatedBalance: number;
}

export interface NextTransactionResponse {
  transactionName: string;
  date: string; // ISO date
  amount: number;
  isIncome: boolean;
  currencyIsoCode: string;
}

export function useCardService() {
  const { token, userId } = useAuth();

  if (!token) throw new Error("User Logged Out (Token not provided)!" + token);

  const withAuthHeader = getAuthHeader(token);

  async function getCurrentBalance(): Promise<CurrentBalanceResponse> {
    return await sendToBackend(
      `general-transactions/current-balance/${userId}`,
      withAuthHeader,
      "Nieudało się pobrać aktualnego salda"
    );
  }

  async function getMonthEstimatedBalance(): Promise<EstimatedBalanceResponse> {
    return await sendToBackend(
      `general-transactions/estimated-balance/${userId}`,
      withAuthHeader,
      "Nie udało się pobrać szacowanego salda"
    );
  }

  async function getNextTransaction(
    isIncome: boolean
  ): Promise<NextTransactionResponse> {
    return await sendToBackend(
      `general-transactions/next-transaction/${userId}?isIncome=${isIncome}`,
      withAuthHeader,
      "Nie udało się pobrać następnej transakcji"
    );
  }

  return {
    getCurrentBalance,
    getMonthEstimatedBalance,
    getNextTransaction,
  };
}
