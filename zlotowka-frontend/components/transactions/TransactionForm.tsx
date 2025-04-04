"use client";

import { useState, useEffect } from "react";
import { TransactionFormProps } from "@/interfaces/transactions/PopupTransactionsProps";
import { TransactionData } from "@/interfaces/transactions/TransactionsData";
import ConfirmButton from "@/components/general/Button";

const inputClass =
  "border-[1px] border-neutral-300 rounded-[5px] px-4 py-2 text-md min-w-76 ";

// TODO: datepicker
export default function TransactionForm({
  transaction,
  onClose,
  header,
  submitButtonText,
  submitButtonIcon,
}: TransactionFormProps) {
  const [isVisible, setIsVisible] = useState(false);
  const [formData, setFormData] = useState<TransactionData>(
    transaction || {
      name: "",
      date: "",
      frequency: "Raz",
      type: "expense",
      amount: "",
      currency: "PLN",
    }
  );

  const handleClose = () => {
    setIsVisible(false);
    setTimeout(() => onClose(), 300);
  };

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

  useEffect(() => {
    setIsVisible(true);
  }, []);

  return (
    <div
      className={`z-[999] w-full h-screen fixed top-0 left-0 flex justify-center items-center select-none transition-opacity duration-200 ${
        isVisible ? "opacity-100" : "opacity-0"
      }`}
    >
      {/* Background */}
      <div
        className="absolute w-full h-full bg-[#818181] opacity-70 transition-opacity duration-300"
        onClick={handleClose}
      ></div>

      {/* Form */}
      <div
        className={`bg-neutral-100 border-[1px] border-[rgba(38,38,38,0.5)] px-8 py-10 rounded-[10px] z-10 transition-all duration-200 ease-in-out transform ${
          isVisible ? "scale-100 opacity-100" : "scale-90 opacity-0"
        }`}
      >
        <div className="mb-6">
          <h2 className="text-2xl font-medium">{header}</h2>
        </div>

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
            className={inputClass}
            type="text"
            placeholder="Wyobraźcie sobie date pickera"
            value={formData.date}
            onChange={handleInputChange}
          />
        </div>

        {/* Frequency select */}
        <div className="py-1">
          <h3 className="text-md my-2 font-medium">Cykliczność</h3>
          <select
            name="frequency"
            value={formData.frequency}
            onChange={handleInputChange}
            className={inputClass + "bg-neutral-100"}
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
            className="border-[1px] border-neutral-300 rounded-[5px] px-4 py-2 text-md w-full"
            type="text"
            placeholder="Kwota"
            value={formData.amount}
            onChange={handleInputChange}
          />
          <select
            name="currency"
            value={formData.currency}
            onChange={handleInputChange}
            className="border-[1px] border-neutral-300 rounded-[5px] px-2 text-md bg-neutral-100"
          >
            <option value="PLN">PLN</option>
            <option value="EUR">EUR</option>
            <option value="USD">USD</option>
          </select>
        </div>

        {/* Button */}
        <ConfirmButton icon={submitButtonIcon} variant="dark" className="mt-7">
          {submitButtonText}
        </ConfirmButton>
      </div>
    </div>
  );
}
