"use client";

import TransactionForm from "@/components/transactions/TransactionForm";
import { AddTransactionProps } from "@/interfaces/transactions/PopupTransactionsProps";
import {
  NewOneTimeTransactionReq,
  useTransactionService,
} from "@/services/TransactionService";
import { useMutation } from "@tanstack/react-query";
import toast from "react-hot-toast";

export default function AddTransaction({
  setShowAddTransaction,
  transactionRefresh,
}: AddTransactionProps) {
  const TransactionService = useTransactionService();
  const magic = useMutation({
    mutationFn: async (data: NewOneTimeTransactionReq) => {
      const newTransactionPromise =
        TransactionService.createNewTransaction(data);
      toast.promise(newTransactionPromise, {
        loading: "Dodawanie transakcji...",
        success: "Transakcja dodana pomyślnie!",
        error: (error) =>
          `Wystąpił błąd podczas dodawania transakcji: ${error.message}`,
      });
      return await newTransactionPromise;
    },
    onSuccess: () => {
      transactionRefresh(); // TODO handle it better
    },
  });
  return (
    <TransactionForm
      onClose={() => {
        setShowAddTransaction(false);
      }}
      onSubmit={(data) => {
        magic.mutate(data);
      }}
      header="Dodaj nową transakcje"
      submitButtonText="Dodaj transakcje"
      submitButtonIcon="add"
    />
  );
}
