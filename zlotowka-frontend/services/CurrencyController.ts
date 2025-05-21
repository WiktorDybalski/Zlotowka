"use client";

import { useAuth } from "@/components/providers/AuthProvider";
import sendToBackend, { getAuthHeader } from "@/lib/sendToBackend";

export interface Currency {
  currencyId: number;
  isoCode: string;
}

export function useCurrencyService() {
  const { token } = useAuth();

  if (!token) throw new Error("User Logged Out (Token not provided)!");

  const withAuthHeader = getAuthHeader(token);

  async function getCurrencyList(): Promise<Array<Currency>> {
    return await sendToBackend(
      `currency/all`,
      withAuthHeader,
      "Nie udało się pobrać listy walut"
    );
  }

  return {
    getCurrencyList,
  };
}
