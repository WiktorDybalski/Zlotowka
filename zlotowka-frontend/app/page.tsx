"use client"

import { useEffect, useState } from "react";
import AddTransaction from "@/components/AddTransaction";

export default function Home() {
  const [data, setData] = useState<string | null>(null);
  const [showAddTransaction, setShowAddTransaction] = useState<boolean>(false);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const response = await fetch("https://zlotowka-621092586366.us-central1.run.app/test");
        const text = await response.text();
        setData(text);
      } catch (error) {
        console.error("Błąd podczas pobierania danych:", error);
      }
    };
    fetchData();
  }, []);

  const toggle = () => {
    setShowAddTransaction(!showAddTransaction);
  };

  return (
      <div className="h-[200vh] flex flex-col justify-center items-center">
        <h1 className="text-4xl -mt-36 mb-4">Scrollable Hello World</h1>
        <h2>{data ? data : "Loading..."}</h2>
        <button className="border-neutral-900 border-2 px-2 py-3 mt-4" onClick={toggle}>Add Transaction</button>
        {showAddTransaction && <AddTransaction setShowAddTransaction={setShowAddTransaction} />}
      </div>
  );
}
