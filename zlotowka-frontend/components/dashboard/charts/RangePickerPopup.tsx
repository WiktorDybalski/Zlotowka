"use client";

import { useState } from "react";
import DatePicker from "@/components/general/DatePicker";
import GenericPopup from "@/components/general/GenericPopup";
import dayjs, { Dayjs } from "dayjs";
import toast from "react-hot-toast";

const inputClass =
    "border-[1px] border-neutral-300 rounded-[5px] px-4 py-2 text-md min-w-76 font-lato";

interface RangePickerPopupProps {
  onClose: () => void;
  onDateChange: (startDate: Dayjs, endDate: Dayjs) => void;
}

export default function RangePickerPopup({ onClose, onDateChange }: RangePickerPopupProps) {
  const [isStartDatePickerOpen, setIsStartDatePickerOpen] = useState(false);
  const [isEndDatePickerOpen, setIsEndDatePickerOpen] = useState(false);
  const [startDate, setStartDate] = useState<Dayjs>(dayjs().subtract(30, "day"));
  const [endDate, setEndDate] = useState<Dayjs>(dayjs());

  const handleConfirm = () => {
    if(startDate > endDate) {
      toast.error("Data końcowa nie może być wcześniej niż początkowa!");
      return;
    }
    onDateChange(startDate, endDate);
    onClose();
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
          title="Wybierz zakres dat"
          onClose={onClose}
          onConfirm={handleConfirm}
          confirmText="Potwierdź"
      >
        <div>
          <div className="py-1">
            <h3 className="text-md my-2 font-medium">Data Początkowa</h3>
            <input
                name="startDate"
                className={inputClass}
                type="text"
                value={startDate ? startDate.format("YYYY-MM-DD") : ""}
                onFocus={handleStartDateClick}
                readOnly
            />
            {isStartDatePickerOpen && (
                <DatePicker
                    isOpen={isStartDatePickerOpen}
                    currentDate={startDate || dayjs()}
                    setIsOpen={setIsStartDatePickerOpen}
                    setDate={(newDate: Dayjs) => setStartDate(newDate)}
                />
            )}
          </div>

          <div className="py-1">
            <h3 className="text-md my-2 font-medium">Data Końcowa</h3>
            <input
                name="endDate"
                className={inputClass}
                type="text"
                value={endDate ? endDate.format("YYYY-MM-DD") : ""}
                onFocus={handleEndDateClick}
                readOnly
            />
            {isEndDatePickerOpen && (
                <DatePicker
                    isOpen={isEndDatePickerOpen}
                    currentDate={endDate || dayjs()}
                    setIsOpen={setIsEndDatePickerOpen}
                    setDate={(newDate: Dayjs) => setEndDate(newDate)}
                />
            )}
          </div>
        </div>
      </GenericPopup>
  );
}