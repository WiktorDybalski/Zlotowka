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
import toast from "react-hot-toast";

const inputClass =
  "border-[1px] border-neutral-300 rounded-[5px] px-4 py-2 text-md min-w-76 ";

const defaultTransactionData: TransactionData = {
  name: "",
  date: dayjs().format("YYYY-MM-DD"),
  frequency: "Raz",
  isIncome: true,
  amount: 0,
  currency: {
    currencyId: 1,
    isoCode: "PLN",
  },
  description: "",
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
  console.log(currencyList);

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
    const { name, value } = e.target;
    setFormData(prevState => ({
      ...prevState,
      [name]: value,
    }));


    if (name === 'amount') {
      const valueWithDot = value.replace(',', '.');
      const parsedValue = parseFloat(valueWithDot);

      if (!Number.isNaN(parsedValue)) {
        setFormData(prevState => ({
          ...prevState,
          amount: parsedValue,
        }));
      }
    }
    if (name === 'currency') {
      const selectedCurrency = currencyList.find(
          (currency) => currency.currencyId === Number(value)
      );
      if (selectedCurrency) {
        setFormData((prev) => ({
          ...prev,
          currency: selectedCurrency,
        }));
      }
    }
  };

  const handleTypeChange = (isIncome: boolean) => {
      setFormData({
        ...formData,
        isIncome: isIncome,
      });
  };

  const toggleDatePicker = () => {
    setIsDatePickerOpen((prev) => !prev);
  };

  const handleSubmit = () => {
    if (isNaN(formData.amount)) {
      toast.error("Price is not a number!");
      return;
    } else if (!formData.amount) {
      toast.error("Price is empty!");
      return;
    } else if (!formData.isIncome) {
      toast.error("Type is not selected!");
      return;
    } else if (!formData.name || formData.name.length < 3) {
      toast.error("Invalid name!");
      return;
    }
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

        <div className="py-1">
          <h3 className="text-md my-2 font-medium">Opis</h3>
          <input
              name="description"
              className={inputClass}
              type="text"
              placeholder="Kilogram ziemniaków"
              value={formData.description}
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
                checked={formData.isIncome === true}
                onChange={() => handleTypeChange(true)}
              />
            </label>
            <h3>Przychód</h3>
          </div>
          <div className="flex gap-x-2">
            <label>
              <input
                type="radio"
                checked={formData.isIncome === false}
                onChange={() => handleTypeChange(false)}
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
            value={formData.currency.isoCode}
            onChange={handleInputChange}
            className="border-[1px] border-neutral-300 rounded-[5px] px-2 text-md bg-background"
          >
            {currencyList.map((currency) => (
                <option key={currency.currencyId} value={currency.currencyId}>
                  {currency.isoCode}
                </option>
            ))}
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
