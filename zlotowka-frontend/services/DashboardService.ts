"use client";

import { useLogin } from "@/components/providers/LoginProvider";
import sendToBackend, { getAuthHeader } from "@/lib/sendToBackend";

export function useDashboardService() {
  const { token } = useLogin();

  if (!token) throw new Error("User Logged Out (Token not provided)!");

  const withAuthHeader = getAuthHeader(token);

  async function getMainChartData(
    userId: number,
    startDate: string,
    endDate: string
  ) {
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

  async function getPieSideChartData(userId: number) {
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
