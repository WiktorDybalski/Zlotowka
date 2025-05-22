"use client";

import LoadingSpinner from "@/components/general/LoadingSpinner";
import { useTransactionService } from "@/services/TransactionService";
import { useState } from "react";
import EditTransactionButton from "./EditTransactionButton";
import EditTransaction from "../EditTransaction";
import { DisplayedGeneralTransaction } from "@/interfaces/transactions/TransactionsData";
import { useQueryWithToast } from "@/lib/data-grabbers";
import dayjs from "dayjs";

// Nowy grid: 4 kolumny: Data, Nazwa, Kwota, Opis
const grid = "grid grid-cols-[15%_15%_30%_1fr] gap-4";

interface TransactionTableProps {
  dateRange?: {
    startDate: string;
    endDate: string;
  };
}

export function TransactionTable({ dateRange }: TransactionTableProps) {
  const [showEditTransaction, setShowEditTransaction] =
    useState<boolean>(false);
  const [transactionForEdit, setTransactionForEdit] =
    useState<DisplayedGeneralTransaction | null>(null);

  const TransactionService = useTransactionService();

  const { data } = useQueryWithToast({
    queryKey: ["transaction", "getTransactions", JSON.stringify(dateRange)],
    queryFn: () => {
      return dateRange
        ? TransactionService.getTransactionsFromRange(
            dateRange.startDate,
            dateRange.endDate
          )
        : TransactionService.getTransactions();
    },
  });

  const transactionList = data?.transactions;
  const today = dayjs();

  return (
    <>
      {showEditTransaction && transactionForEdit && (
        <EditTransaction
          setShowEditTransaction={setShowEditTransaction}
          transaction={transactionForEdit}
        />
      )}
      <section className="h-full text-xs xl:text-sm">
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
            {transactionList ? (
              transactionList.map((transaction, idx) => (
                <div
                  key={idx}
                  className={`${grid} py-2 border-b last:border-0`}
                >
                  <div>
                    {dayjs(transaction.date).isBefore(today, "day") ? (
                      <del>{transaction.date}</del>
                    ) : (
                      transaction.date
                    )}
                  </div>
                  <div>
                    <span
                      className={
                        transaction.isIncome
                          ? "bg-green-200 px-3 py-1 rounded"
                          : "bg-red-200 px-2 pb-1 rounded"
                      }
                    >
                      {transaction.isIncome ? "+" : "-"}
                      {transaction.amount} {transaction.currency.isoCode}
                    </span>
                  </div>
                  <div>
                    {transaction.period != "ONCE" ? (
                      <span className="bg-blue-200 px-3 py-1 rounded">
                        {`↻ ${transaction.name}`}
                      </span>
                    ) : (
                      transaction.name
                    )}
                  </div>
                  <div className=" w-full flex items-center justify-between ">
                    <span>{transaction.description}</span>
                    <div className="md:pr-2">
                      <EditTransactionButton
                        onClick={() => {
                          setTransactionForEdit(transaction);
                          setShowEditTransaction(true);
                        }}
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
    </>
  );
}
