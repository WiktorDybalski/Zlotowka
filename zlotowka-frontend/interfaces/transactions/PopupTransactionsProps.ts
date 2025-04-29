import { TransactionData } from "@/interfaces/transactions/TransactionsData";
import { OneTimeTransaction } from "@/services/TransactionService";

export interface AddTransactionProps {
  setShowAddTransaction: React.Dispatch<React.SetStateAction<boolean>>;
  transactionRefresh: () => void;
}

export interface EditTransactionProps {
  transaction: OneTimeTransaction;
  setShowEditTransaction: React.Dispatch<React.SetStateAction<boolean>>;
  transactionRefresh: () => void;
}

export interface TransactionFormProps {
  transaction?: TransactionData;
  onSubmit: (data: TransactionData) => void;
  onClose: () => void;
  header: string;
  submitButtonText: string;
  submitButtonIcon: string;
}
