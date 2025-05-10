"use client";

import TransactionForm from "@/components/transactions/TransactionForm";
import { AddTransactionProps } from "@/interfaces/transactions/PopupTransactionsProps";
import {
  useTransactionService,
} from "@/services/TransactionService";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import {TransactionData} from "@/interfaces/transactions/TransactionsData";
import {submitTransaction} from "@/services/ProcessTransaction";

export default function AddTransaction({
                                         setShowAddTransaction,
                                       }: AddTransactionProps) {
  const TransactionService = useTransactionService();
  const queryClient = useQueryClient();

  const mutation = useMutation({
    mutationFn: (data: TransactionData) =>
        submitTransaction(data, TransactionService),
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