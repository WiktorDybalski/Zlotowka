import TransactionForm from "@/components/transactions/TransactionForm";
import { EditTransactionProps } from "@/interfaces/transactions/PopupTransactionsProps";
import {
  useTransactionService,
} from "@/services/TransactionService";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import {TransactionData} from "@/interfaces/transactions/TransactionsData";
import {submitTransaction} from "@/services/ProcessTransaction";

export default function EditTransaction({
  transaction,
  setShowEditTransaction,
}: EditTransactionProps) {
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
      transaction={{ ...transaction, frequency: "Raz" }}
      onCloseAction={() => setShowEditTransaction(false)}
      onSubmitAction={(data) => mutation.mutate(data)}
      header="Edytuj transakcje"
      submitButtonText="Edytuj transakcje"
      submitButtonIcon="edit"
    />
  );
}
