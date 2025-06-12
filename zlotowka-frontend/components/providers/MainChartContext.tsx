import {
  createContext,
  ReactNode,
  useContext,
  useEffect,
  useState,
} from "react";
import dayjs, { Dayjs } from "dayjs";
import { MainChartContextType } from "@/interfaces/dashboard/charts/MainChart";

const MainChartContext = createContext<MainChartContextType | undefined>(
  undefined,
);

export function MainChartProvider({ children }: { children: ReactNode }) {
  const [startDate, setStartDate] = useState<Dayjs>(() => {
    const stored = localStorage.getItem("startDate");
    return stored ? dayjs(stored) : dayjs().subtract(30, "day");
  });

  const [endDate, setEndDate] = useState<Dayjs>(() => {
    const stored = localStorage.getItem("endDate");
    return stored ? dayjs(stored) : dayjs();
  });

  const [showDreams, setShowDreams] = useState<boolean>(() => {
    const storedValue = localStorage.getItem("showDreams");
    return storedValue ? JSON.parse(storedValue) : false;
  });

  const [equalDates, setEqualDates] = useState<boolean>(() => {
    const storedValue = localStorage.getItem("equalDates");
    return storedValue ? JSON.parse(storedValue) : false;
  });

  const [showSubDreams, setShowSubDreams] = useState<boolean>(() => {
    const storedValue = localStorage.getItem("showSubDreams");
    return storedValue ? JSON.parse(storedValue) : false;
  });

  // Zapisuj do localStorage po zmianie
  useEffect(() => {
    localStorage.setItem("startDate", startDate.toISOString());
    localStorage.setItem("endDate", endDate.toISOString());
    localStorage.setItem("showDreams", JSON.stringify(showDreams));
    localStorage.setItem("showSubDreams", JSON.stringify(showSubDreams));
    localStorage.setItem("equalDates", JSON.stringify(equalDates));
  }, [startDate, endDate, showDreams, showSubDreams, equalDates]);

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
        setEqualDates,
      }}
    >
      {children}
    </MainChartContext.Provider>
  );
}

export function useMainChartContext() {
  const context = useContext(MainChartContext);
  if (!context) {
    throw new Error(
      "useMainChartContext must be used within a MainChartProvider",
    );
  }
  return context;
}
