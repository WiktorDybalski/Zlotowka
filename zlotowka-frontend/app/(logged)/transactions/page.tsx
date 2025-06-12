"use client";

import DarkButton from "@/components/DarkButton";
import GenericCard from "@/components/dashboard/cards/generic/GenericCard";
import AddTransaction from "@/components/transactions/AddTransaction";
import PickInterval from "@/components/transactions/PickInterval";
import { TransactionTable } from "@/components/transactions/table/TransactionTable";
import dayjs from "dayjs";
import { useState } from "react";

export default function TransactionsPage() {
  const [showAddTransaction, setShowAddTransaction] = useState(false);
  const [showPickIntervalPopup, setShowPickIntervalPopup] = useState(false);
  const [selectedDates, setSelectedDates] = useState({
    startDate: dayjs().subtract(30, "day"),
    endDate: dayjs(),
  });

  return (
    <>
      {showAddTransaction && (
        <AddTransaction setShowAddTransaction={setShowAddTransaction} />
      )}
      {showPickIntervalPopup && (
        <PickInterval
          onCloseAction={() => setShowPickIntervalPopup(false)}
          onConfirmDates={(startDate, endDate) => {
            setSelectedDates({
              startDate: startDate,
              endDate: endDate,
            });
            setShowPickIntervalPopup(false);
          }}
          title="Wybierz zakres dat"
        />
      )}
      <main className="relative w-full min-h-screen px-6 py-5 lg:px-20 2xl:px-40">
        <div className="flex flex-col sm:flex-row items-center justify-between gap-y-4 sm:gap-y-0 gap-x-10">
          <h2 className="text-3xl md:text-4xl lg:text-5xl font-bold">
            Twoje transakcje
          </h2>

          <div className="w-full sm:w-52  sm:mt-5 flex flex-col gap-3">
            <DarkButton
              icon={"add"}
              text={"Dodaj transakcje"}
              onClick={() => {
                setShowAddTransaction(!showAddTransaction);
              }}
            />
            <DarkButton
              icon={"calendar_month"}
              text={"Wybierz zakres dat"}
              onClick={() => {
                setShowPickIntervalPopup(!showPickIntervalPopup);
              }}
            />
          </div>
        </div>

        <div className="w-full h-[70vh] mt-5 overflow-hidden">
          <GenericCard>
            <TransactionTable
              dateRange={{
                startDate: selectedDates.startDate.format("YYYY-MM-DD"),
                endDate: selectedDates.endDate.format("YYYY-MM-DD"),
              }}
            />
          </GenericCard>
        </div>
      </main>
    </>
  );
}
