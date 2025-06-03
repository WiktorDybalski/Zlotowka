import TransactionForm from "@/components/transactions/TransactionForm";
import { EditTransactionProps } from "@/interfaces/transactions/PopupTransactionsProps";
import { useTransactionService } from "@/services/TransactionService";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import {
  RecurringTransaction,
  TransactionData,
} from "@/interfaces/transactions/TransactionsData";
import toast from "react-hot-toast";
import { useQueryWithToast } from "@/lib/data-grabbers";

export default function EditTransaction({
  transaction,
  setShowEditTransaction,
}: EditTransactionProps) {
  const TransactionService = useTransactionService();
  const queryClient = useQueryClient();

  const { data: fetchedTransaction } = useQueryWithToast({
    queryKey: [
      "transaction",
      "getRecurringTransaction",
      transaction.transactionId,
    ],
    queryFn: async () => {
      return transaction.period === "ONCE"
        ? transaction
        : await TransactionService.getRecurringTransaction(
            transaction.transactionId
          );
    },
  });

  const mutation = useMutation({
    mutationFn: async (data: TransactionData) => {
      const d = { ...transaction, ...data };
      const isRecurring = data.frequency.code !== "No period";
      const res = isRecurring
        ? TransactionService.editRecurringTransaction(d)
        : TransactionService.editOneTimeTransaction(d);
      toast.promise(res as Promise<unknown>, {
        loading: `Edytowanie transakcji ${
          isRecurring ? "rekurencyjnej" : "jednorazowej"
        }...`,
        success: `Transakcja ${
          isRecurring ? "rekurencyjna" : "jednorazowa"
        } zmodyfikowana pomyślnie!`,
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

  const magicTransactionDelete = useMutation({
    mutationFn: async ({
      transactionId,
      isRecursive,
    }: {
      transactionId: number;
      isRecursive: boolean;
    }) => {
      const res = isRecursive
        ? TransactionService.deleteRecurringTransaction(transactionId)
        : TransactionService.deleteOneTimeTransaction(transactionId);
      toast.promise(res, {
        loading: "Usuwanie transakcji...",
        success: "Transakcja usunięta pomyślnie!",
        error: (error) =>
          `Wystąpił błąd podczas usuwania transakcji: ${error.message}`,
      });
      return await res;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["transaction"] });
      queryClient.invalidateQueries({ queryKey: ["cardService"] }); //on dashboard
      queryClient.invalidateQueries({ queryKey: ["dashboard"] }); //on dashboard
      queryClient.invalidateQueries({ queryKey: ["allTransactionsFromRange"] });
    },
  });

  return (
    <>
      {fetchedTransaction && (
        <TransactionForm
          transaction={{
            ...transaction,
            ...fetchedTransaction,
            frequency:
              transaction.period != "ONCE"
                ? {
                    code: (fetchedTransaction as RecurringTransaction).interval,
                    name: (fetchedTransaction as RecurringTransaction).interval,
                  }
                : {
                    code: "No period",
                    name: "Raz",
                  },
            startDate: (fetchedTransaction as RecurringTransaction)
              .firstPaymentDate,
            endDate: (fetchedTransaction as RecurringTransaction)
              .finalPaymentDate,
          }}
          onCloseAction={() => setShowEditTransaction(false)}
          onSubmitAction={(data) => mutation.mutate(data)}
          onDeleteAction={() => {
            magicTransactionDelete.mutate({
              transactionId: transaction.transactionId,
              isRecursive: transaction.period != "ONCE",
            });
          }}
          header="Edytuj transakcje"
          submitButtonText="Edytuj transakcje"
          submitButtonIcon="edit"
          isEdditingRecurringTransaction={
            transaction.period != "ONCE" ? "yes" : "no"
          }
        />
      )}
    </>
  );
}
