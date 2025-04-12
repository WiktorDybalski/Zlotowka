"use client";

import { useEffect, useState } from "react";
import DatePicker from "@/components/general/DatePicker";
import DarkButton from "@/components/DarkButton";
import dayjs, { Dayjs } from "dayjs";
import toast from "react-hot-toast";

const inputClass =
    "border-[1px] border-neutral-300 rounded-[5px] px-4 py-2 text-md min-w-76 font-lato";

interface RangePickerPopupProps {
  onClose: () => void;
  onDateChange: (startDate: Dayjs, endDate: Dayjs) => void;
}

export default function RangePickerPopup({ onClose, onDateChange }: RangePickerPopupProps) {
  const [isVisible, setIsVisible] = useState(false);
  const [isStartDatePickerOpen, setIsStartDatePickerOpen] = useState(false);
  const [isEndDatePickerOpen, setIsEndDatePickerOpen] = useState(false);
  const [startDate, setStartDate] = useState<Dayjs>(dayjs().subtract(30, "day"));
  const [endDate, setEndDate] = useState<Dayjs>(dayjs());

  const handleClose = () => {
    setIsVisible(false);
    setTimeout(() => onClose(), 300);
  };

  const handleConfirm = () => {
    if(startDate > endDate) {
      toast.error("Data końcowa nie może być wcześniej niż początkowa!");
      return;
    }
    onDateChange(startDate, endDate);
    setStartDate(startDate);
    setEndDate(endDate);
    handleClose();
  };

  const handleStartDateClick = () => {
    setIsStartDatePickerOpen(true);
    setIsEndDatePickerOpen(false);
  };

  const handleEndDateClick = () => {
    setIsEndDatePickerOpen(true);
    setIsStartDatePickerOpen(false);
  };

  useEffect(() => {
    setIsVisible(true);
  }, []);

  return (
      <div
          className={`z-[999] w-full min-h-screen fixed top-0 left-0 flex justify-center items-center select-none transition-opacity duration-200 ${isVisible ? "opacity-100" : "opacity-0"}`}
      >
        <div
            className="absolute w-full h-full bg-[#818181] opacity-70 transition-opacity duration-300"
            onClick={handleClose}
        ></div>

        <div
            className={`bg-background border-[1px] border-[rgba(38,38,38,0.5)] px-8 py-10 rounded-[10px] z-10 transition-all duration-200 ease-in-out transform ${isVisible ? "scale-100 opacity-100" : "scale-90 opacity-0"}`}
        >
          <div className="mb-6">
            <h2 className="text-2xl font-medium">Wybierz zakres dat</h2>
          </div>

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

          <div className="mt-4">
            <DarkButton text="Potwierdź" onClick={handleConfirm} />
          </div>
        </div>
      </div>
  );
}
