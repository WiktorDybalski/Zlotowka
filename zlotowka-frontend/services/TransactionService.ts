"use client";

import { useAuth } from "@/components/providers/AuthProvider";
import sendToBackend, { getAuthHeader } from "@/lib/sendToBackend";

export interface Currency {
  currencyId: number;
  isoCode: string;
}

export interface OneTimeTransaction {
  transactionId: number;
  userId: number;
  name: string;
  amount: number;
  currency: Currency;
  isIncome: boolean;
  date: string; // ISO date string (np. "2025-04-28")
  description: string;
}

export function useTransactionService() {
  const { token } = useAuth();

  if (!token) throw new Error("User Logged Out (Token not provided)!");

  const withAuthHeader = getAuthHeader(token);

  const userId = 1;

  async function getTransactions(): Promise<Array<OneTimeTransaction>> {
    return await sendToBackend(
      `onetime-transaction/all/${userId}`,
      withAuthHeader,
      "Failed to fetch transactions"
    );
  }

  return {
    getTransactions,
  };
}
