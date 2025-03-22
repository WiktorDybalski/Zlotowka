import { TransactionData } from "@/interfaces/transactions/TransactionsData";

export interface AddTransactionProps {
  setShowAddTransaction: React.Dispatch<React.SetStateAction<boolean>>;
}

export interface EditTransactionProps {
  transaction: TransactionData;
  setShowEditTransaction: React.Dispatch<React.SetStateAction<boolean>>;
}

export interface TransactionFormProps {
  transaction?: TransactionData;
  onClose: () => void;
  header: string;
  submitButtonText: string;
  submitButtonIcon: string;
}
