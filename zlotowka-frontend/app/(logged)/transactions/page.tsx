"use client";

import DarkButton from "@/components/DarkButton";
import GenericCard from "@/components/dashboard/cards/generic/GenericCard";
import AddTransaction from "@/components/transactions/AddTransaction";
import { TransactionTable } from "@/components/transactions/table/TransactionTable";
import { useState } from "react";

export default function TransactionsPage() {
  const [showAddTransaction, setShowAddTransaction] = useState(false);

  return (
    <>
      {showAddTransaction && (
        <AddTransaction setShowAddTransaction={setShowAddTransaction} />
      )}
      <main className="relative w-full min-h-screen px-6 py-10 lg:px-20 2xl:px-40">
        <div className="flex flex-col sm:flex-row items-center justify-between gap-y-4 sm:gap-y-0 gap-x-10">
          <h2 className="text-3xl md:text-4xl lg:text-5xl font-bold">
            Twoje transakcje
          </h2>

          <div className="w-full sm:w-52 h-10 sm:mt-5">
            <DarkButton
              icon={"add"}
              text={"Dodaj transakcje"}
              onClick={() => {
                setShowAddTransaction(!showAddTransaction);
              }}
            />
          </div>
        </div>

        <div className="w-full h-[80vh] mt-5 sm:mt-10 overflow-hidden">
          <GenericCard>
            <TransactionTable />
          </GenericCard>
        </div>
      </main>
    </>
  );
}
