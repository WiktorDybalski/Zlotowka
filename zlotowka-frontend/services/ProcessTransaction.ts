import toast from "react-hot-toast";
import {TransactionData} from "@/interfaces/transactions/TransactionsData";

export type ReturnTypeTransactionService = {
  createNewTransaction: (data: TransactionData) => Promise<unknown>;
  createNewRecurringTransaction: (data: TransactionData) => Promise<unknown>;
};

export async function submitTransaction(
    data: TransactionData,
    TransactionService: ReturnTypeTransactionService,
): Promise<void> {

  let promise;
  if (data.frequency === "Raz") {
    promise = TransactionService.createNewTransaction(data);
  } else {
    promise = TransactionService.createNewRecurringTransaction(data);
  }

  toast.promise(promise, {
    loading: "Dodawanie transakcji...",
    success: "Transakcja dodana pomyślnie!",
    error: (error: Error) => `Błąd przy dodawaniu transakcji: ${error.message}`,
  });

  await promise;
}