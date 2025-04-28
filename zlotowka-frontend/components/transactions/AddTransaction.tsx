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
        alert("Dodano transakcje: " + JSON.stringify(data));
        const fixedData: NewOneTimeTransactionReq = {
          name: data.name,
          date: data.date,
          amount: 100, //TODO pole jest stringiem i mozna wpisac stringa
          currency: {
            //TODO to jest objectem a nie stringiem, zaciagnac sobie z backendu serwisem
            currencyId: 1,
            isoCode: "PLN",
          },
          isIncome: true, //TODO niech to bedzie bool
          description: "some description", //TODO nie ma pola w formularzu
        };

        magic.mutate(fixedData);
      }}
      header="Dodaj nową transakcje"
      submitButtonText="Dodaj transakcje"
      submitButtonIcon="add"
    />
  );
}
