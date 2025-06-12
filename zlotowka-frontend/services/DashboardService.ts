"use client";

import { useAuth } from "@/components/providers/AuthProvider";
import sendToBackend, { getAuthHeader } from "@/lib/sendToBackend";

export interface SinglePlotData {
  date: string; // IDO date
  amount: number;
}

export type MainChartDataResponse = Array<SinglePlotData>;

export interface MonthlySummaryResponse {
  monthlyIncome: number;
  monthlyExpenses: number;
  monthlyBalance: number;
}

export function useDashboardService() {
  const { token, userId } = useAuth();

  if (!token) throw new Error("User Logged Out (Token not provided)!");

  const withAuthHeader = getAuthHeader(token);

  async function getMainChartData(
    startDate: string,
    endDate: string,
  ): Promise<MainChartDataResponse> {
    return await sendToBackend(
      `general-transactions/plot-data`,
      {
        ...withAuthHeader,
        method: "POST",
        body: JSON.stringify({
          userId,
          startDate,
          endDate,
        }),
      },
      "Nie udało się pobrać danych do wykresu",
    );
  }

  async function getPieSideChartData(): Promise<MonthlySummaryResponse> {
    return await sendToBackend(
      `general-transactions/monthly-summary/${userId}`,
      withAuthHeader,
      "Nie udało się pobrać danych do wykresu kołowego",
    );
  }

  return {
    getMainChartData,
    getPieSideChartData,
  };
}
