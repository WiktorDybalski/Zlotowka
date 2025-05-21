"use client";

import { useMainChartContext } from "../providers/MainChartContext";
import { TransactionTable } from "../transactions/table/TransactionTable";

export default function DashboardTransactionTable() {
  const { startDate, endDate } = useMainChartContext();

  return (
    <TransactionTable
      dateRange={{
        startDate: startDate.format("YYYY-MM-DD"),
        endDate: endDate.format("YYYY-MM-DD"),
      }}
    />
  );
}
