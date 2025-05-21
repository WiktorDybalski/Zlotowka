import {createContext, ReactNode, useEffect, useState} from "react";
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
  setShowSubDreams: () => {},
  equalDates: false,
  setEqualDates: () => {}
});

export default function MainChartProvider({ children }: { children: ReactNode }) {
  const [startDate, setStartDate] = useState<Dayjs>(dayjs().subtract(30, "day"));
  const [endDate, setEndDate] = useState<Dayjs>(dayjs());

  const [showDreams, setShowDreams] = useState<boolean>(() => {
    const storedValue = localStorage.getItem('showDreams');
    return storedValue ? JSON.parse(storedValue) : false;
  });

  const [equalDates, setEqualDates] = useState<boolean>(() => {
    const storedValue = localStorage.getItem('equalDates');
    return storedValue ? JSON.parse(storedValue) : false;
  });

  const [showSubDreams, setShowSubDreams] = useState<boolean>(() => {
    const storedValue = localStorage.getItem('showSubDreams');
    return storedValue ? JSON.parse(storedValue) : false;
  });

  useEffect(() => {
    localStorage.setItem('showDreams', JSON.stringify(showDreams));
    localStorage.setItem('showSubDreams', JSON.stringify(showSubDreams));
    localStorage.setItem('equalDates', JSON.stringify(equalDates));
  }, [showDreams, showSubDreams, equalDates]);

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
            equalDates,
            setEqualDates
          }}
      >
        {children}
      </MainChartContext.Provider>
  );
}
