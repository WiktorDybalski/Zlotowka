"use client";

import DarkButton from "@/components/DarkButton";
import AddTransaction from "@/components/transactions/AddTransaction";
import { useState } from "react";

export default function TransactionsPage() {
  const [showAddTransaction, setShowAddTransaction] = useState(false);

  return (
    <>
      {showAddTransaction && (
        <AddTransaction setShowAddTransaction={setShowAddTransaction} />
      )}
      <main className="relative w-full h-screen px-6 py-12 lg:px-20 lg:py-20 2xl:px-40">
        <h2 className="text-3xl md:text-4xl lg:text-5xl font-bold">
          Twoje transakcje
        </h2>
        <div className="w-full h-[70vh] mt-10 overflow-hidden">
          {/*<GenericCard>*/}
          {/*  <TransactionTable />*/}
          {/*</GenericCard>*/}
        </div>

        <div className="w-full sm:w-52 h-10 mt-10">
          <DarkButton
            icon={"add"}
            text={"Dodaj transakcje"}
            onClick={() => {
              setShowAddTransaction(!showAddTransaction);
            }}
          />
        </div>
      </main>
    </>
  );
}
