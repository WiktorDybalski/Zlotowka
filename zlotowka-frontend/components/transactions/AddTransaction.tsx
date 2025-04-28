import TransactionForm from "@/components/transactions/TransactionForm";
import { AddTransactionProps } from "@/interfaces/transactions/PopupTransactionsProps";

export default function AddTransaction({
  setShowAddTransaction,
  transactionRefresh,
}: AddTransactionProps) {
  return (
    <TransactionForm
      onClose={(data) => {
        setShowAddTransaction(false);
        alert("Dodano transakcje: " + JSON.stringify(data));
        transactionRefresh(); // TODO handle it better
      }}
      header="Dodaj nową transakcje"
      submitButtonText="Dodaj transakcje"
      submitButtonIcon="add"
    />
  );
}
