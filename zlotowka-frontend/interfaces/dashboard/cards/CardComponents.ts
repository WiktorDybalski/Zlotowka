import {JSX} from "react";

export type CardId = "nextExpense" | "pinnedDream" | "monthForecast" | "currentBalance";

export type CardComponents = {
  [key in CardId]: JSX.Element;
};