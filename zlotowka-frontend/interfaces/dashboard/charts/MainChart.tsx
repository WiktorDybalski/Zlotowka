
import {LabelProps, TooltipProps} from "recharts";
import {NameType, ValueType} from "recharts/types/component/DefaultTooltipContent";
import {Dayjs} from "dayjs";
import {Dispatch, SetStateAction} from "react";
import {OneTimeTransaction} from "@/interfaces/transactions/TransactionsData";

export interface CustomChartTooltipProps extends TooltipProps<ValueType, NameType> {
  transactions: OneTimeTransaction[];
}

export interface MainChartContextType {
  startDate: Dayjs;
  setStartDate: Dispatch<SetStateAction<Dayjs>>;
  endDate: Dayjs;
  setEndDate: Dispatch<SetStateAction<Dayjs>>;
  showDreams: boolean;
  setShowDreams: Dispatch<SetStateAction<boolean>>;
  showSubDreams: boolean;
  setShowSubDreams: Dispatch<SetStateAction<boolean>>;
}

export interface MainChartPopupProps {
  onCloseAction: () => void;
}

export interface CustomLabelProps extends LabelProps {
  total: number;
}