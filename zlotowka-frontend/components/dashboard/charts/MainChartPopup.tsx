"use client";

import {useContext, useState} from "react";
import DatePicker from "@/components/general/DatePicker";
import GenericPopup from "@/components/general/GenericPopup";
import dayjs, {Dayjs} from "dayjs";
import toast from "react-hot-toast";
import {MainChartContext} from "@/components/dashboard/charts/MainChartContext";
import {MainChartPopupProps} from "@/interfaces/dashboard/charts/MainChart";
import {useQueryClient} from "@tanstack/react-query";

const inputClass =
    "border-[1px] border-neutral-300 rounded-[5px] px-4 py-2 text-md min-w-76 font-lato";

const dateButtonClass = "px-3 py-1 border-[1px] rounded border-border-color transition-colors duration-200 hover:bg-accent hover:text-background hover:cursor-pointer"

export default function MainChartPopup({ onCloseAction }: MainChartPopupProps) {
  const {setStartDate, setEndDate, showDreams, setShowDreams, showSubDreams, setShowSubDreams} = useContext(MainChartContext);
  const [isStartDatePickerOpen, setIsStartDatePickerOpen] = useState(false);
  const [isEndDatePickerOpen, setIsEndDatePickerOpen] = useState(false);
  const [tempStartDate, setTempStartDate] = useState<Dayjs>(dayjs().subtract(30, "day"));
  const [tempEndDate, setTempEndDate] = useState<Dayjs>(dayjs());
  const [tempShowDreams, setTempShowDreams] = useState(showDreams);
  const [tempShowSubDreams, setTempShowSubDreams] = useState(showSubDreams);
  const [isForward, setIsForward] = useState(false);
  const queryClient = useQueryClient();

  const handleConfirm = () => {
    if (tempStartDate > tempEndDate) {
      toast.error("Data końcowa nie może być wcześniej niż początkowa!");
      return;
    }

    setStartDate(tempStartDate);
    setEndDate(tempEndDate);
    setShowDreams(tempShowDreams);
    setShowSubDreams(tempShowSubDreams);

    queryClient.invalidateQueries({ queryKey: ["allTransactionsFromRange"] })
    onCloseAction();
  };

  const handleDateRangeChange = (unit: "day" | "week" | "month" | "year") => {
    const refDate = dayjs();
    const newDate = refDate.add(isForward ? 1 : -1, unit);

    setTempStartDate(isForward ? refDate : newDate);
    setTempEndDate(isForward ? newDate : refDate);
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
                  className={`${dateButtonClass} w-28`}>
                {tempShowDreams ? "Pokaż" : "Schowaj"}
              </button>
            </div>
          </div>

          <div className="py-1 flex justify-between">
            <h3 className="my-2">Podmarzenia</h3>
            <div className="flex gap-2 flex-wrap font-lato">
              <button
                  onClick={() => setTempShowSubDreams(!tempShowSubDreams)}
                  className={`${dateButtonClass} w-28`}>
                {tempShowSubDreams ? "Pokaż" : "Schowaj"}
              </button>
            </div>
          </div>

          <div className="py-1">
            <h3 className="my-2">Szybki wybór</h3>
            <div className="flex gap-2 flex-wrap font-lato">
              <button onClick={() => handleDateRangeChange("day")} className={dateButtonClass}>1D</button>
              <button onClick={() => handleDateRangeChange("week")} className={dateButtonClass}>1T</button>
              <button onClick={() => handleDateRangeChange("month")} className={dateButtonClass}>1M</button>
              <button onClick={() => handleDateRangeChange("year")} className={dateButtonClass}>1R</button>
              <button onClick={() => setIsForward(!isForward)} className={`${dateButtonClass} w-22`}>{isForward ? "W Przód" : "W tył"}</button>
            </div>
          </div>

          <div className="py-1">
            <h3 className="my-2">Data Początkowa</h3>
            <input
                name="startDate"
                className={inputClass}
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
                className={inputClass}
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