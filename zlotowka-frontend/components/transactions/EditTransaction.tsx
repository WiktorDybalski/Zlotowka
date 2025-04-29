import TransactionForm from "@/components/transactions/TransactionForm";
import { EditTransactionProps } from "@/interfaces/transactions/PopupTransactionsProps";
import {
  EdittedOneTimeTransactionReq,
  useTransactionService,
} from "@/services/TransactionService";
import { useMutation } from "@tanstack/react-query";
import toast from "react-hot-toast";

export default function EditTransaction({
  transaction,
  setShowEditTransaction,
  transactionRefresh,
}: EditTransactionProps) {
  const TransactionService = useTransactionService();

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
      transactionRefresh(); // TODO handle it better
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
