import type { ChartConfig } from "@/components/ui/chart";

const isDark =
  typeof window !== "undefined" &&
  document.documentElement.classList.contains("dark");
const strokeIncome = isDark ? "#858383" : "#262626";
const strokeExpense = isDark ? "#fafafa" : "#e9e9e9";

export const MainChartConfig = {
  amount: {
    label: "Stan konta",
    color: "#262626",
  },
} satisfies ChartConfig;

export const PieChartConfig = {
  value: {
    label: "Value",
  },
  Income: {
    label: "Przychody",
    color: strokeIncome,
  },
  Expenses: {
    label: "Wydatki",
    color: strokeExpense,
  },
} satisfies ChartConfig;
