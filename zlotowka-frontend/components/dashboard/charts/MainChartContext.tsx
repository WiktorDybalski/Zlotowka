import { createContext, ReactNode, useState } from "react";
import dayjs, { Dayjs } from "dayjs";
import {MainChartContextType} from "@/interfaces/dashboard/charts/MainChart";

export const MainChartContext = createContext<MainChartContextType>({
  startDate: dayjs(),
  setStartDate: () => {},
  endDate: dayjs(),
  setEndDate: () => {},
  showDreams: false,
  setShowDreams: () => {},
  showSubDreams: false,
  setShowSubDreams: () => {}
});

export default function MainChartProvider({ children }: { children: ReactNode }) {
  const [startDate, setStartDate] = useState<Dayjs>(dayjs().subtract(30, "day"));
  const [endDate, setEndDate] = useState<Dayjs>(dayjs());
  const [showDreams, setShowDreams] = useState<boolean>(false);
  const [showSubDreams, setShowSubDreams] = useState<boolean>(false);

  return (
      <MainChartContext.Provider
          value={{
            startDate,
            setStartDate,
            endDate,
            setEndDate,
            showDreams,
            setShowDreams,
            showSubDreams,
            setShowSubDreams,
          }}
      >
        {children}
      </MainChartContext.Provider>
  );
}
