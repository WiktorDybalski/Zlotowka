import {JSX} from "react";

export interface CardDefinition {
  id: CardId;
  label: string;
}

export type CardId = "nextExpense" | "nextIncome" | "pinnedDream" | "monthForecast" | "currentBalance";

export const AVAILABLE_CARDS: CardDefinition[] = [
  { id: "nextExpense", label: "Następny wydatek" },
  { id: "pinnedDream", label: "Przypięte marzenie" },
  { id: "monthForecast", label: "Prognoza miesięczna" },
  { id: "currentBalance", label: "Bieżące saldo" },
  { id: "nextIncome", label: "Następny przychód" }
];

export type CardComponents = {
  [key in CardId]: JSX.Element;
};