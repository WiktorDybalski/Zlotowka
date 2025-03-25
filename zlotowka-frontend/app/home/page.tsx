"use client"

import {useState} from "react";
import AddTransaction from "@/components/transactions/AddTransaction";
import EditTransaction from "@/components/transactions/EditTransaction";
import {sampleTransaction} from "@/mocks/SampleTransaction";

export default function Home() {
  const [showAddTransaction, setShowAddTransaction] = useState<boolean>(false);
  const [showEditTransaction, setShowEditTransaction] = useState<boolean>(false);

  return (
      <div className="h-[200vh] flex flex-col justify-center items-center">
        <h1 className="text-4xl -mt-36 mb-4">Scrollable Hello World</h1>

        {/* temporary stuff */}
        <button
            className="border-neutral-900 border-2 px-2 py-3 mt-4"
            onClick={() => setShowAddTransaction(true)}
        >
          Add Transaction
        </button>
        <button
            className="border-neutral-900 border-2 px-2 py-3 mt-4"
            onClick={() => setShowEditTransaction(true)}
        >
          Edit Transaction
        </button>

        {showAddTransaction && (
            <AddTransaction setShowAddTransaction={setShowAddTransaction} />
        )}

        {showEditTransaction && (
            <EditTransaction
                transaction={sampleTransaction}
                setShowEditTransaction={setShowEditTransaction}
            />
        )}
      </div>
  );
}