"use client";

import { timePeriod } from "@/components/dashboard/components/MainChartPopup";
import { useState } from "react";
import dayjs from "dayjs";

interface QuickDatesProps {
  setTempDate: (date: dayjs.Dayjs) => void;
}

export default function QuickDates({ setTempDate }: QuickDatesProps) {
  const [quickDate, setQuickDate] = useState<timePeriod | null>(null);
  const [isForward, setIsForward] = useState(false);

  const updateDateRange = (unit: timePeriod | null, forward: boolean) => {
    if (!unit) {
      return;
    }
    const refDate = dayjs();
    const newDate = refDate.add(forward ? 1 : -1, unit);

    setTempDate(forward ? refDate : newDate);
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
    return `chart-options-buttons ${
      quickDate === period ? "bg-accent text-background" : ""
    }`;
  };

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
      {false && (
        <button
          onClick={handleDirectionToggle}
          className={`chart-options-buttons w-22 ${
            true ? "!bg-accent/50 !text-background/50 !cursor-default" : ""
          }`}
          disabled={true}
        >
          {isForward ? "W Przód" : "W tył"}
        </button>
      )}
    </div>
  );
}
