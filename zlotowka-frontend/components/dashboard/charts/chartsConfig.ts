import type {ChartConfig} from "@/components/ui/chart";

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
    color: "#262626",
  },
  Expenses: {
    label: "Wydatki",
    color: "#e9e9e9",
  },
} satisfies ChartConfig;