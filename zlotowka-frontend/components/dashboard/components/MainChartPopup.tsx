"use client";

import { useState } from "react";
import DatePicker from "@/components/general/DatePicker";
import GenericPopup from "@/components/general/GenericPopup";
import dayjs, { Dayjs } from "dayjs";
import toast from "react-hot-toast";
import { MainChartPopupProps } from "@/interfaces/dashboard/charts/MainChart";
import { useQueryClient } from "@tanstack/react-query";
import { useMainChartContext } from "@/components/providers/MainChartContext";
import QuickDates from "@/components/dashboard/components/QuickDates";

export type timePeriod = "day" | "week" | "month" | "year";

export default function MainChartPopup({ onCloseAction }: MainChartPopupProps) {
  const {
    startDate,
    endDate,
    setStartDate,
    setEndDate,
    showDreams,
    setShowDreams,
    showSubDreams,
    setShowSubDreams,
  } = useMainChartContext();

  const [isStartDatePickerOpen, setIsStartDatePickerOpen] = useState(false);
  const [isEndDatePickerOpen, setIsEndDatePickerOpen] = useState(false);

  const [tempStartDate, setTempStartDate] = useState<Dayjs>(
    startDate || dayjs()
  );
  const [tempEndDate, setTempEndDate] = useState<Dayjs>(
    endDate || dayjs().add(30, "day")
  );

  console.log("MainChartPopup startDate", startDate, "endDate", endDate);

  const [tempShowDreams, setTempShowDreams] = useState(showDreams);
  const [tempShowSubDreams, setTempShowSubDreams] = useState(showSubDreams);

  const queryClient = useQueryClient();

  const handleConfirm = () => {
    if (tempStartDate > tempEndDate) {
      toast.error("Data końcowa nie może być wcześniej niż początkowa!");
      return;
    }

    setStartDate(dayjs(tempStartDate.toISOString()));
    setEndDate(dayjs(tempEndDate.toISOString()));

    setShowDreams(tempShowDreams);
    setShowSubDreams(tempShowSubDreams);

    queryClient.invalidateQueries({ queryKey: ["allTransactionsFromRange"] });
    onCloseAction();
  };

  const handleStartDateClick = () => {
    setIsStartDatePickerOpen(true);
    setIsEndDatePickerOpen(false);
  };

  const handleEndDateClick = () => {
    setIsEndDatePickerOpen(true);
    setIsStartDatePickerOpen(false);
  };

  return (
    <GenericPopup
      title="Opcje wykresu"
      onCloseAction={onCloseAction}
      onConfirmAction={handleConfirm}
      confirmText="Potwierdź"
    >
      <div className="text-md font-medium relative">
        <div className="py-1 flex justify-between">
          <h3 className="my-2">Marzenia</h3>
          <div className="flex gap-2 flex-wrap font-lato">
            <button
              onClick={() => setTempShowDreams(!tempShowDreams)}
              className={`chart-options-buttons w-28`}
            >
              {tempShowDreams ? "Schowaj" : "Pokaż"}
            </button>
          </div>
        </div>

        <div className="py-1 flex justify-between">
          <h3 className="my-2">Podmarzenia</h3>
          <div className="flex gap-2 flex-wrap font-lato">
            <button
              onClick={() => setTempShowSubDreams(!tempShowSubDreams)}
              className={`chart-options-buttons w-28`}
            >
              {tempShowSubDreams ? "Schowaj" : "Pokaż"}
            </button>
          </div>
        </div>

        <div className="py-1">
          <h3 className="my-2">Szybki wybór twoja stara</h3>
          <QuickDates
            setTempStartDate={setTempStartDate}
            setTempEndDate={setTempEndDate}
          />
        </div>

        <div className="py-1">
          <h3 className="my-2">Data Początkowa</h3>
          <input
            name="startDate"
            className={"form-input"}
            type="text"
            value={tempStartDate.format("YYYY-MM-DD")}
            onFocus={handleStartDateClick}
            readOnly
          />
          {isStartDatePickerOpen && (
            <DatePicker
              isOpen={isStartDatePickerOpen}
              currentDate={tempStartDate.format("YYYY-MM-DD")}
              setIsOpenAction={setIsStartDatePickerOpen}
              setDateAction={setTempStartDate}
            />
          )}
        </div>

        <div className="py-1">
          <h3 className="my-2">Data Końcowa</h3>
          <input
            name="endDate"
            className={"form-input"}
            type="text"
            value={tempEndDate.format("YYYY-MM-DD")}
            onFocus={handleEndDateClick}
            readOnly
          />
          {isEndDatePickerOpen && (
            <DatePicker
              isOpen={isEndDatePickerOpen}
              currentDate={tempEndDate.format("YYYY-MM-DD")}
              setIsOpenAction={setIsEndDatePickerOpen}
              setDateAction={setTempEndDate}
            />
          )}
        </div>
      </div>
    </GenericPopup>
  );
}
