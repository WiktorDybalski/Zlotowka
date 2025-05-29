"use client";

import { useState } from "react";
import DatePicker from "@/components/general/DatePicker";
import GenericPopup from "@/components/general/GenericPopup";
import dayjs, { Dayjs } from "dayjs";
import toast from "react-hot-toast";
import QuickDates from "@/components/dreams/QuickDates";

export type timePeriod = "day" | "week" | "month" | "year";

export interface PickDreamDatePopupProps {
  onCloseAction: () => void;
  onConfirmAction?: (date: Dayjs) => void;
}

export default function PickDreamDatePopup({
  onCloseAction,
  onConfirmAction,
}: PickDreamDatePopupProps) {
  const [isDatePickerOpen, setIsDatePickerOpen] = useState(false);

  const [tempDate, setTempDate] = useState<Dayjs>(dayjs());

  const handleConfirm = () => {
    if (tempDate > dayjs()) {
      toast.error("Wybrana data nie może być datą z przyszłości!");
      return;
    }
    onCloseAction();
    onConfirmAction?.(tempDate);
  };

  const handleStartDateClick = () => {
    setIsDatePickerOpen(true);
  };

  return (
    <GenericPopup
      title="Wybierz datę ukończenia"
      onCloseAction={onCloseAction}
      onConfirmAction={handleConfirm}
      confirmText="Potwierdź"
    >
      <div
        className={`text-md font-medium relative ${
          isDatePickerOpen ? "h-[450px]" : ""
        }`}
      >
        <div className="py-1">
          <h3 className="my-2">Szybki wybór przedziału</h3>
          <QuickDates setTempDate={setTempDate} />
        </div>

        <div className="py-1">
          <h3 className="my-2">Data Ukończenia</h3>
          <input
            name="date"
            className={"form-input"}
            type="text"
            value={tempDate.format("YYYY-MM-DD")}
            onFocus={handleStartDateClick}
            readOnly
          />
          {isDatePickerOpen && (
            <DatePicker
              isOpen={isDatePickerOpen}
              currentDate={tempDate.format("YYYY-MM-DD")}
              setIsOpenAction={setIsDatePickerOpen}
              setDateAction={setTempDate}
            />
          )}
        </div>
      </div>
    </GenericPopup>
  );
}
