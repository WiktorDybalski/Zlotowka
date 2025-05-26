"use client";

import LoadingSpinner from "@/components/general/LoadingSpinner";
import { useTransactionService } from "@/services/TransactionService";
import { useMemo, useState } from "react";
import EditTransactionButton from "./EditTransactionButton";
import EditTransaction from "../EditTransaction";
import { DisplayedGeneralTransaction } from "@/interfaces/transactions/TransactionsData";
import { useQueryWithToast } from "@/lib/data-grabbers";
import dayjs from "dayjs";

// Dla md+ używamy 4 kolumn, a dla mniejszych tylko 3
const headerGrid =
  "grid grid-cols-3 grid-cols-[20%_25%_45%]  md:grid-cols-[15%_15%_30%_1fr] gap-4";

interface TransactionTableProps {
  dateRange?: {
    startDate: string;
    endDate: string;
  };
}

function UppArrowIcon() {
  return <span className="material-symbols text-sm">arrow_upward</span>;
}

function DownArrowIcon() {
  return <span className="material-symbols text-sm">arrow_downward</span>;
}

export function TransactionTable({ dateRange }: TransactionTableProps) {
  const [showEditTransaction, setShowEditTransaction] =
    useState<boolean>(false);
  const [transactionForEdit, setTransactionForEdit] =
    useState<DisplayedGeneralTransaction | null>(null);

  // Sortowanie
  const [sortField, setSortField] = useState<string>("date");
  const [sortDirection, setSortDirection] = useState<"asc" | "desc">("desc");

  const onSort = (field: string) => {
    if (sortField === field) {
      setSortDirection(sortDirection === "asc" ? "desc" : "asc");
    } else {
      setSortField(field);
      setSortDirection("asc");
    }
  };

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

  const sortedTransactionList = useMemo(() => {
    if (!transactionList || !sortField) return transactionList;
    return [...transactionList].sort((a, b) => {
      if (sortField === "date") {
        return sortDirection === "asc"
          ? dayjs(a.date).diff(dayjs(b.date))
          : dayjs(b.date).diff(dayjs(a.date));
      }
      if (sortField === "amount") {
        return sortDirection === "asc"
          ? a.amount - b.amount
          : b.amount - a.amount;
      }
      let aValue: string, bValue: string;
      if (sortField === "name") {
        aValue = a.name.toLowerCase();
        bValue = b.name.toLowerCase();
      } else if (sortField === "description") {
        aValue = a.description.toLowerCase();
        bValue = b.description.toLowerCase();
      } else {
        return 0;
      }
      if (aValue < bValue) return sortDirection === "asc" ? -1 : 1;
      if (aValue > bValue) return sortDirection === "asc" ? 1 : -1;
      return 0;
    });
  }, [transactionList, sortField, sortDirection]);

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
          {/* Nagłówki gridu – dla mniejszych niż md kolumna "Opis" jest ukryta */}
          <div
            className={`${headerGrid} font-bold border-b pb-2`}
            style={{ overflowY: "auto", scrollbarGutter: "stable" }}
          >
            <div
              className="cursor-pointer flex items-center"
              onClick={() => onSort("date")}
            >
              Data{" "}
              {sortField === "date" &&
                (sortDirection === "asc" ? (
                  <UppArrowIcon />
                ) : (
                  <DownArrowIcon />
                ))}
            </div>
            <div
              className="cursor-pointer flex items-center"
              onClick={() => onSort("amount")}
            >
              Kwota{" "}
              {sortField === "amount" &&
                (sortDirection === "asc" ? (
                  <UppArrowIcon />
                ) : (
                  <DownArrowIcon />
                ))}
            </div>
            <div
              className="cursor-pointer flex items-center"
              onClick={() => onSort("name")}
            >
              Nazwa Transakcji{" "}
              {sortField === "name" &&
                (sortDirection === "asc" ? (
                  <UppArrowIcon />
                ) : (
                  <DownArrowIcon />
                ))}
            </div>
            <div
              className="cursor-pointer items-center hidden md:flex"
              onClick={() => onSort("description")}
            >
              Opis{" "}
              {sortField === "description" &&
                (sortDirection === "asc" ? (
                  <UppArrowIcon />
                ) : (
                  <DownArrowIcon />
                ))}
            </div>
          </div>
          {/* Wiersze danych – kolumna "Opis" ukryta na mniejszych ekranach */}
          <div className="h-full overflow-y-auto overflow-x-hidden">
            {sortedTransactionList ? (
              sortedTransactionList.map((transaction, idx) => (
                <div
                  key={idx}
                  className={`${headerGrid} py-2 border-b last:border-0`}
                >
                  <div className="font-lato items-center flex">
                    {dayjs(transaction.date).isBefore(today, "day") ? (
                      <del>{transaction.date}</del>
                    ) : (
                      transaction.date
                    )}
                  </div>
                  <div className="font-lato items-center flex">
                    <span
                      className={
                        transaction.isIncome
                          ? "bg-green-200 px-3 py-1 rounded "
                          : "bg-red-200 px-2 pb-1 rounded "
                      }
                    >
                      {transaction.isIncome ? "+" : "-"}
                      {transaction.amount} {transaction.currency.isoCode}
                    </span>
                  </div>
                  <div className="hidden md:flex items-center">
                    {transaction.period !== "ONCE" ? (
                      <span className="bg-blue-200 px-3 py-1 rounded">
                        {`↻ ${transaction.name}`}
                      </span>
                    ) : (
                      transaction.name
                    )}
                  </div>
                  <div className="flex items-center justify-between md:hidden">
                    {transaction.period !== "ONCE" ? (
                      <span className="bg-blue-200 px-3 py-1 rounded">
                        {`↻ ${transaction.name}`}
                      </span>
                    ) : (
                      transaction.name
                    )}
                    <EditTransactionButton
                      onClick={() => {
                        setTransactionForEdit(transaction);
                        setShowEditTransaction(true);
                      }}
                    />
                  </div>
                  <div className="hidden md:flex w-full  items-center justify-between">
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
