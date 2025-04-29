"use client";

import { useAuth } from "@/components/providers/AuthProvider";
import sendToBackend, { getAuthHeader } from "@/lib/sendToBackend";

export function useDashboardService() {
  const { token, userId } = useAuth();

  if (!token) throw new Error("User Logged Out (Token not provided)!");

  const withAuthHeader = getAuthHeader(token);

  async function getMainChartData(startDate: string, endDate: string) {
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
      "Failed to fetch main chart data"
    );
  }

  async function getPieSideChartData() {
    return await sendToBackend(
      `general-transactions/monthly-summary/${userId}`,
      withAuthHeader,
      "Failed to fetch pie side chart data"
    );
  }

  return {
    getMainChartData,
    getPieSideChartData,
  };
}
