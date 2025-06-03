"use client";

import TransactionForm from "@/components/transactions/TransactionForm";
import { AddTransactionProps } from "@/interfaces/transactions/PopupTransactionsProps";
import { useTransactionService } from "@/services/TransactionService";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { TransactionData } from "@/interfaces/transactions/TransactionsData";
import toast from "react-hot-toast";

export default function AddTransaction({
  setShowAddTransaction,
}: AddTransactionProps) {
  const TransactionService = useTransactionService();
  const queryClient = useQueryClient();

  const mutation = useMutation({
    mutationFn: async (data: TransactionData) => {
      const res =
        data.frequency.code === "No period"
          ? TransactionService.createNewOneTimeTransaction(data)
          : TransactionService.createNewRecurringTransaction(data);
      const isRecurring = data.frequency.code !== "No period";
      toast.promise(res as Promise<unknown>, {
        loading: `Dodawanie transakcji ${
          isRecurring ? "rekurencyjnej" : "jednorazowej"
        }...`,
        success: `Transakcja ${
          isRecurring ? "rekurencyjnej" : "jednorazowej"
        } dodana pomyślnie!`,
        error: (error: Error) =>
          `Błąd przy dodawaniu transakcji ${
            isRecurring ? "rekurencyjnej" : "jednorazowej"
          }: ${error.message}`,
      });
      return await res;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["transaction"] });
      queryClient.invalidateQueries({ queryKey: ["cardService"] });
      queryClient.invalidateQueries({ queryKey: ["dashboard"] });
      queryClient.invalidateQueries({ queryKey: ["allTransactionsFromRange"] });
    },
  });

  return (
    <TransactionForm
      onCloseAction={() => setShowAddTransaction(false)}
      onSubmitAction={(data) => mutation.mutate(data)}
      header="Dodaj nową transakcję"
      submitButtonText="Dodaj transakcję"
      submitButtonIcon="add"
    />
  );
}
