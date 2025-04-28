"use client";

import LoadingSpinner from "@/components/general/LoadingSpinner";
import { useTransactionService } from "@/services/TransactionService";
import { useMutation } from "@tanstack/react-query";
import { useEffect } from "react";
import toast from "react-hot-toast";
import EditTransactionButton from "./EditTransactionButton";
import DeleteTransactionButton from "./DeleteTransactionButton";

interface TransactionTableProps {
  refresh: number;
}

// Nowy grid: 4 kolumny: Data, Nazwa, Kwota, Opis
const grid = "grid grid-cols-[10%_10%_30%_1fr] gap-4";

export function TransactionTable({ refresh }: TransactionTableProps) {
  const TransactionService = useTransactionService();

  const magicTransactionList = useMutation({
    mutationFn: async () => {
      const res = await TransactionService.getTransactions();
      return res;
    },
    onError: (error) => {
      toast.error(`Nie udało się pobrać tranzakcji: ${error.message}`);
    },
  });

  const magicTransactionDelete = useMutation({
    mutationFn: async (transactionId: number) => {
      const res = TransactionService.deleteTransaction(transactionId);
      toast.promise(res, {
        loading: "Usuwanie transakcji...",
        success: "Transakcja usunięta pomyślnie!",
        error: (error) =>
          `Wystąpił błąd podczas usuwania transakcji: ${error.message}`,
      });
      return await res;
    },
    onSuccess: () => {
      magicTransactionList.mutate();
    },
  });

  useEffect(() => {
    magicTransactionList.mutate();
  }, [refresh]);

  return (
    <section className="h-full text-xs md:text-sm xl:text-base">
      <div className="h-full overflow-hidden p-6">
        {/* Nagłówki gridu */}
        <div className={`${grid} font-bold border-b pb-2`}>
          <div>Data</div>
          <div>Kwota</div>
          <div>Nazwa Transakcji</div>
          <div>Opis</div>
        </div>
        {/* Wiersze danych */}
        <div className="h-full overflow-auto">
          {magicTransactionList.data ? (
            magicTransactionList.data.map((transaction, idx) => (
              <div key={idx} className={`${grid} py-2 border-b last:border-0`}>
                <div>{transaction.date}</div>
                <div>
                  <span
                    className={
                      transaction.isIncome
                        ? "bg-green-200 px-2 pb-1 rounded"
                        : "bg-red-200 px-2 pb-1 rounded"
                    }
                  >
                    {transaction.isIncome ? "+" : "-"}
                    {transaction.amount} {transaction.currency.isoCode}
                  </span>
                </div>
                <div>{transaction.name}</div>
                <div className=" w-full flex items-center justify-between ">
                  <span>{transaction.description}</span>
                  <div className="flex items-center gap-2 md:pr-5">
                    <EditTransactionButton onClick={() => alert("edycja!")} />
                    <DeleteTransactionButton
                      onClick={() =>
                        magicTransactionDelete.mutate(transaction.transactionId)
                      }
                    />
                  </div>
                </div>
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
