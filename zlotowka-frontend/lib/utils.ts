import { clsx, type ClassValue } from "clsx";
import { twMerge } from "tailwind-merge";
import {ChartDreamsData} from "@/services/DreamsService";

export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs));
}

export function getRoundedDomain(
    dreams: ChartDreamsData[],
    step: number = 100,
    filterType?: 'PLAN' | 'SUBPLAN'
): [number, number] {
  const filteredDreams = filterType
      ? dreams.filter(d => d.planType === filterType)
      : dreams;

  const dreamAmounts = filteredDreams?.map(d => d.requiredAmount * 1.05) ?? [];

  if (dreamAmounts.length === 0) return [0, step];

  const min = Math.min(...dreamAmounts);
  const max = Math.max(...dreamAmounts);
  const roundedMin = Math.floor(min / step) * step;
  const roundedMax = Math.ceil(max / step) * step;

  return [roundedMin, roundedMax];
}

export function mapFrequencyToPeriodType(frequency: string): string {
  switch (frequency) {
    case "Codziennie":
      return "P1D"
    case "Co tydzień":
      return "P1W"
    case "Co miesiąc":
      return "P1M"
    case "Co rok":
      return "P1Y"
    default:
      return "";
  }
}