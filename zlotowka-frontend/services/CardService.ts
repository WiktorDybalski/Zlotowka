"use client";

import { useLogin } from "@/components/providers/LoginProvider";
import sendToBackend, { getAuthHeader } from "@/lib/sendToBackend";

export function useCardService() {
  const { token } = useLogin();

  if (!token) throw new Error("User Logged Out (Token not provided)!" + token);

  const withAuthHeader = getAuthHeader(token);

  async function getCurrentBalance(userId: number) {
    return await sendToBackend(
      `general-transactions/current-balance/${userId}`,
      withAuthHeader,
      "Failed to fetch current balance"
    );
  }

  async function getMonthEstimatedBalance(userId: number) {
    return await sendToBackend(
      `general-transactions/estimated-balance/${userId}`,
      withAuthHeader,
      "Failed to fetch estimated balance"
    );
  }

  async function getNextTransaction(userId: number, isIncome: boolean) {
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
