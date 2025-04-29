import TransactionForm from "@/components/transactions/TransactionForm";
import { EditTransactionProps } from "@/interfaces/transactions/PopupTransactionsProps";
import {
  EdittedOneTimeTransactionReq,
  useTransactionService,
} from "@/services/TransactionService";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import toast from "react-hot-toast";

export default function EditTransaction({
  transaction,
  setShowEditTransaction,
}: EditTransactionProps) {
  const TransactionService = useTransactionService();
  const queryClient = useQueryClient();

  const magic = useMutation({
    mutationFn: async (transaction: EdittedOneTimeTransactionReq) => {
      const res = TransactionService.editTransaction(transaction);
      toast.promise(res, {
        loading: "Edycja transakcji...",
        success: "Transakcja edytowana pomyślnie!",
        error: (error) =>
          `Wystąpił błąd podczas edytowania transakcji: ${error.message}`,
      });
      return await res;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["transaction"] });
      queryClient.invalidateQueries({ queryKey: ["cardService"] }); //on dashboard
      queryClient.invalidateQueries({ queryKey: ["dashboard"] });
    },
  });
  return (
    <TransactionForm
      transaction={{ ...transaction, frequency: "Raz" }} //TODO: change this!
      onClose={() => setShowEditTransaction(false)}
      onSubmit={(data) => {
        magic.mutate({ ...data, transactionId: transaction.transactionId });
      }}
      header="Edytuj transakcje"
      submitButtonText="Edytuj transakcje"
      submitButtonIcon="edit"
    />
  );
}
