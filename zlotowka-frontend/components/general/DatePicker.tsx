"use client";

import dayjs, { Dayjs } from "dayjs";
import "dayjs/locale/pl";
import { DateCalendar, LocalizationProvider } from "@mui/x-date-pickers";
import React from "react";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";

dayjs.locale("pl");

interface DatePickerProps {
  isOpen: boolean;
  currentDate: Dayjs;
  setIsOpen: React.Dispatch<React.SetStateAction<boolean>>;
  setDate: (newDate: Dayjs) => void;
}

export default function DatePicker({
  isOpen,
  currentDate,
  setIsOpen,
  setDate,
}: DatePickerProps) {
  return (
    <LocalizationProvider dateAdapter={AdapterDayjs} adapterLocale="pl">
      {isOpen && (
        <DateCalendar
          className="border-border-color border-[1px] rounded-[5px] min-w-76 absolute z-[9999] bg-background"
          value={currentDate}
          onChange={(newValue) => {
            setDate(newValue);
            setIsOpen(false);
          }}
          sx={{
            width: 300,
            ".Mui-selected": {
              backgroundColor: "#262626 !important",
              "&:focus": {
                backgroundColor: "#262626 !important",
              },
            },
          }}
        />
      )}
    </LocalizationProvider>
  );
}
