"use client";

import dayjs, { Dayjs } from "dayjs";
import "dayjs/locale/pl";
import { DateCalendar, LocalizationProvider } from "@mui/x-date-pickers";
import React from "react";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";

dayjs.locale("pl");

interface DatePickerProps {
  isOpen: boolean;
  currentDate: string;
  setIsOpenAction: React.Dispatch<React.SetStateAction<boolean>>;
  setDateAction: (newDate: Dayjs) => void;
}

export default function DatePicker({
  isOpen,
  currentDate,
  setIsOpenAction,
  setDateAction,
}: DatePickerProps) {
  const isDark = typeof window !== 'undefined' && document.documentElement.classList.contains('dark');
  return (
        <LocalizationProvider dateAdapter={AdapterDayjs} adapterLocale="pl">
          {isOpen && (
            <DateCalendar
              className="border-border-color border-[1px] rounded-[5px] max-w-67 lg:max-w-76 absolute z-[9999] bg-background dark:bg-accent"
              value={dayjs(currentDate)}
              onChange={(newValue) => {
                setDateAction(newValue);
                setIsOpenAction(false);
              }}
              sx={{
                width: "300",
                overflow: "hidden",
                color: isDark ? "#fafafa" : "#212121",
                "& .MuiDayCalendar-weekDayLabel": {
                  color: isDark ? "#fafafa" : "#212121",
                },
                "& .MuiPickersArrowSwitcher-button": {
                  color: isDark ? "#fafafa" : "#212121",
                },
                "& .MuiPickersDay-root": {
                  color: isDark ? "#fafafa" : "#212121",
                },
                "& .MuiPickersCalendarHeader-switchViewButton" : {
                  color: isDark ? "#fafafa" : "#212121",
                },
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

