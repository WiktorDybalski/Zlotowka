"use client";

import { useAuth } from "@/components/providers/AuthProvider";
import sendToBackend, { getAuthHeader } from "@/lib/sendToBackend";

export function useCardService() {
  const { token, userId } = useAuth();

  if (!token) throw new Error("User Logged Out (Token not provided)!" + token);

  const withAuthHeader = getAuthHeader(token);

  async function getCurrentBalance() {
    return await sendToBackend(
      `general-transactions/current-balance/${userId}`,
      withAuthHeader,
      "Failed to fetch current balance"
    );
  }

  async function getMonthEstimatedBalance() {
    return await sendToBackend(
      `general-transactions/estimated-balance/${userId}`,
      withAuthHeader,
      "Failed to fetch estimated balance"
    );
  }

  async function getNextTransaction(isIncome: boolean) {
    return await sendToBackend(
      `general-transactions/next-transaction/${userId}?isIncome=${isIncome}`,
      withAuthHeader,
      "Failed to fetch next transaction"
    );
  }

  return {
    getCurrentBalance,
    getMonthEstimatedBalance,
    getNextTransaction,
  };
}
