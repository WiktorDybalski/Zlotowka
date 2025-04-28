"use client";

import { useState } from "react";
import { TransactionFormProps } from "@/interfaces/transactions/PopupTransactionsProps";
import { TransactionData } from "@/interfaces/transactions/TransactionsData";
import ConfirmButton from "@/components/general/Button";
import dayjs from "dayjs";
import DatePicker from "@/components/general/DatePicker";
import GenericPopup from "@/components/general/GenericPopup";
import { useQuery } from "@tanstack/react-query";
import { useCurrencyService } from "@/services/CurrencyController";

const inputClass =
  "border-[1px] border-neutral-300 rounded-[5px] px-4 py-2 text-md min-w-76 ";

const defaultTransactionData: TransactionData = {
  name: "",
  date: dayjs().format("YYYY-MM-DD"),
  frequency: "Raz",
  type: "expense",
  amount: "",
  currency: "PLN",
};

export default function TransactionForm({
  transaction,
  onClose,
  header,
  submitButtonText,
  submitButtonIcon,
  onSubmit,
}: TransactionFormProps) {
  const [isDatePickerOpen, setIsDatePickerOpen] = useState(false);
  const [formData, setFormData] = useState<TransactionData>(
    transaction || defaultTransactionData
  );

  const CurrencyService = useCurrencyService();

  const {
    data: currencyList,
    isLoading: isCurrenciesLoading,
    isError: isCurrenciecError,
    isSuccess: isCurrenciesSuccess,
  } = useQuery({
    queryKey: ["currencyData"],
    queryFn: CurrencyService.getCurrencyList,
  });

  if (isCurrenciesSuccess) {
    console.log("Waluty załadowane pomyślnie!");
  }

  if (isCurrenciesLoading) {
    console.log("Ładowanie walut...");
  }

  if (isCurrenciecError) {
    console.log("Wystąpił błąd podczas ładowania walut!");
  }

  const handleInputChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>
  ) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    });
  };

  const handleTypeChange = (type: "income" | "expense") => {
    setFormData({
      ...formData,
      type,
    });
  };

  const toggleDatePicker = () => {
    setIsDatePickerOpen((prev) => !prev);
  };

  const handleSubmit = () => {
    // TODO add validation
    onClose();
    onSubmit(formData); // Call the onSubmit function with the form data
  };

  return (
    <GenericPopup
      title={header}
      onClose={onClose}
      showConfirm={false} // We'll use our custom button instead
    >
      <>
        {/* Name */}
        <div className="py-1">
          <h3 className="text-md my-2 font-medium">Nazwa</h3>
          <input
            name="name"
            className={inputClass}
            type="text"
            placeholder="Zakupy spożywcze"
            value={formData.name}
            onChange={handleInputChange}
          />
        </div>

        {/* Date picker */}
        <div className="py-1">
          <h3 className="text-md my-2 font-medium">Data</h3>
          <input
            name="date"
            className={inputClass + " font-lato"}
            type="text"
            value={formData.date}
            onChange={handleInputChange}
            onClick={toggleDatePicker}
          />
          <DatePicker
            isOpen={isDatePickerOpen}
            currentDate={dayjs(formData.date)}
            setIsOpen={setIsDatePickerOpen}
            setDate={(newDate) =>
              setFormData((prev) => ({
                ...prev,
                date: dayjs(newDate).format("YYYY-MM-DD"),
              }))
            }
          />
        </div>

        {/* Frequency select */}
        <div className="py-1">
          <h3 className="text-md my-2 font-medium">Cykliczność</h3>
          <select
            name="frequency"
            value={formData.frequency}
            onChange={handleInputChange}
            className={inputClass + " bg-background"}
          >
            <option value="Raz">Raz</option>
            <option value="Codziennie">Codziennie</option>
            <option value="Co tydzień">Co tydzień</option>
            <option value="Co miesiąc">Co miesiąc</option>
          </select>
        </div>

        {/* Income and expense radio's */}
        <div className="flex gap-x-2 my-4">
          <div className="flex gap-x-2">
            <label>
              <input
                type="radio"
                checked={formData.type === "income"}
                onChange={() => handleTypeChange("income")}
              />
            </label>
            <h3>Przychód</h3>
          </div>
          <div className="flex gap-x-2">
            <label>
              <input
                type="radio"
                checked={formData.type === "expense"}
                onChange={() => handleTypeChange("expense")}
              />
            </label>
            <h3>Wydatek</h3>
          </div>
        </div>

        {/* Price and currency */}
        <div className="flex gap-x-2 min-w-72">
          <input
            name="amount"
            className="border-[1px] border-neutral-300 rounded-[5px] px-4 py-2 text-md font-lato w-full"
            type="text"
            placeholder="Kwota"
            value={formData.amount}
            onChange={handleInputChange}
          />
          <select
            name="currency"
            value={formData.currency}
            onChange={handleInputChange}
            className="border-[1px] border-neutral-300 rounded-[5px] px-2 text-md bg-background"
          >
            <option value="PLN">PLN</option>
            <option value="EUR">EUR</option>
            <option value="USD">USD</option>
          </select>
        </div>

        {/* Button */}
        <ConfirmButton
          icon={submitButtonIcon}
          variant="dark"
          className="mt-7"
          onClick={handleSubmit}
        >
          {submitButtonText}
        </ConfirmButton>
      </>
    </GenericPopup>
  );
}
