"use client";

import LoadingSpinner from "@/components/general/LoadingSpinner";
import { useTransactionService } from "@/services/TransactionService";
import { useMutation } from "@tanstack/react-query";
import { useEffect } from "react";
import toast from "react-hot-toast";

interface TransactionTableProps {
  refresh: number;
}

export function TransactionTable({ refresh }: TransactionTableProps) {
  const { getTransactions } = useTransactionService();

  const magic = useMutation({
    mutationFn: async () => {
      const res = await getTransactions();
      return res;
    },
    onError: (error) => {
      toast.error(`Nie udało się pobrać tranzakcji: ${error.message}`);
    },
  });

  useEffect(() => {
    magic.mutate();
  }, [refresh]);

  return (
    <section className="h-full text-xs md:text-sm lg:text-base">
      <div className="h-full overflow-hidden p-6">
        {/* Nagłówki gridu */}
        <div className="grid grid-cols-5 gap-4 font-bold border-b pb-2">
          <div>Data</div>
          <div>Nazwa Transakcji</div>
          <div>Kwota</div>
          <div>Typ</div>
          <div>Opis</div>
        </div>
        {/* Wiersze danych */}
        <div className="h-full overflow-auto">
          {magic.data ? (
            magic.data.map((transaction, idx) => (
              <div
                key={idx}
                className="grid grid-cols-5 gap-4 py-2 border-b last:border-0"
              >
                <div>{transaction.date}</div>
                <div>{transaction.name}</div>
                <div>
                  <span className={"mr-1"}>{transaction.amount}</span>
                  <span>{transaction.currency.isoCode}</span>
                </div>
                <div>{transaction.isIncome ? "Przychód" : "Wydatek"}</div>
                <div>{transaction.description}</div>
              </div>
            ))
          ) : (
            <LoadingSpinner />
          )}
        </div>
      </div>
    </section>
  );
}
