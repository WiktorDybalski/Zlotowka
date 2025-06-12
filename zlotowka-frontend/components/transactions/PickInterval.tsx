"use client";

import { useState } from "react";
import DatePicker from "@/components/general/DatePicker";
import GenericPopup from "@/components/general/GenericPopup";
import dayjs, { Dayjs } from "dayjs";
import toast from "react-hot-toast";
import QuickDates from "@/components/dashboard/components/QuickDates";

export type timePeriod = "day" | "week" | "month" | "year";

interface PickIntervalProps {
  onCloseAction: () => void;
  onConfirmDates?: (startDate: Dayjs, endDate: Dayjs) => void;
  initialStartDate?: Dayjs;
  initialEndDate?: Dayjs;
  title?: string;
}

export default function PickInterval({
  onCloseAction,
  onConfirmDates,
  initialStartDate,
  initialEndDate,
  title = "Wybierz zakres dat",
}: PickIntervalProps) {
  const [isStartDatePickerOpen, setIsStartDatePickerOpen] = useState(false);
  const [isEndDatePickerOpen, setIsEndDatePickerOpen] = useState(false);

  const [tempStartDate, setTempStartDate] = useState<Dayjs>(
    initialStartDate || dayjs().subtract(30, "day")
  );
  const [tempEndDate, setTempEndDate] = useState<Dayjs>(
    initialEndDate || dayjs()
  );

  const handleConfirm = () => {
    if (tempStartDate > tempEndDate) {
      toast.error("Data końcowa nie może być wcześniej niż początkowa!");
      return;
    }

    // Wywołujemy callback z wybranymi datami
    if (onConfirmDates) {
      onConfirmDates(tempStartDate, tempEndDate);
    }

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
      title={title}
      onCloseAction={onCloseAction}
      onConfirmAction={handleConfirm}
      confirmText="Potwierdź"
    >
      <div className="text-md font-medium relative">
        <div className="py-1">
          <h3 className="my-2">Szybki wybór przedziału</h3>
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
