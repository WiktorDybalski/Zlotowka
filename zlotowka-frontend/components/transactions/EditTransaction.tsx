import TransactionForm from "@/components/transactions/TransactionForm";
import {EditTransactionProps} from "@/interfaces/transactions/PopupTransactionsProps";

export default function EditTransaction({transaction, setShowEditTransaction}: EditTransactionProps) {
  return (
      <TransactionForm
          transaction={transaction}
          onClose={() => setShowEditTransaction(false)}
          header="Edytuj transakcje"
          submitButtonText="Edytuj transakcje"
          submitButtonIcon="edit"
      />
  )
}