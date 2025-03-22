import TransactionForm from "@/components/transactions/TransactionForm";
import { AddTransactionProps } from "@/interfaces/transactions/PopupTransactionsProps";

export default function AddTransaction({
  setShowAddTransaction,
}: AddTransactionProps) {
  return (
    <TransactionForm
      onClose={() => setShowAddTransaction(false)}
      header="Dodaj nową transakcje"
      submitButtonText="Dodaj transakcje"
      submitButtonIcon="add"
    />
  );
}
