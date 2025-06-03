"use client"

import {timePeriod} from "@/components/dashboard/components/MainChartPopup";
import {useState} from "react";
import dayjs from "dayjs";

export default function QuickDates( { setTempStartDate, setTempEndDate }) {
  const [quickDate, setQuickDate] = useState<timePeriod | null>(null);
  const [isForward, setIsForward] = useState(false);

  const updateDateRange = (unit: timePeriod | null, forward: boolean) => {
    if (!unit) {
      return;
    }
    const refDate = dayjs();
    const newDate = refDate.add(forward ? 1 : -1, unit);

    setTempStartDate(forward ? refDate : newDate);
    setTempEndDate(forward ? newDate : refDate);
  };

  const handleQuickDateUnitChange = (unit: timePeriod) => {
    setQuickDate(unit);
    updateDateRange(unit, isForward);
  };

  const handleDirectionToggle = () => {
    const newIsForward = !isForward;
    setIsForward(newIsForward);
    updateDateRange(quickDate, newIsForward);
  };

  const getButtonClass = (period: timePeriod) => {
    return `chart-options-buttons ${quickDate === period ? "!bg-accent text-background dark:!bg-veryDark" : ""}`;
  }

  return (
      <div className="flex gap-1 lg:gap-2 flex-wrap font-lato">
        <button
            onClick={() => handleQuickDateUnitChange("day")}
            className={getButtonClass("day")}
        >
          1D
        </button>
        <button
            onClick={() => handleQuickDateUnitChange("week")}
            className={getButtonClass("week")}
        >
          1T
        </button>
        <button
            onClick={() => handleQuickDateUnitChange("month")}
            className={getButtonClass("month")}
        >
          1M
        </button>
        <button
            onClick={() => handleQuickDateUnitChange("year")}
            className={getButtonClass("year")}
        >
          1R
        </button>
        <button
            onClick={handleDirectionToggle}
            className={`chart-options-buttons w-22`}
        >
          {isForward ? "W Przód" : "W tył" }
        </button>
      </div>
  )
}