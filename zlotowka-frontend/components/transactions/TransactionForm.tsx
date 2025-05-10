"use client";

import {useEffect, useRef, useState} from "react";
import { TransactionFormProps } from "@/interfaces/transactions/PopupTransactionsProps";
import { TransactionData } from "@/interfaces/transactions/TransactionsData";
import ConfirmButton from "@/components/general/Button";
import dayjs from "dayjs";
import DatePicker from "@/components/general/DatePicker";
import GenericPopup from "@/components/general/GenericPopup";
import { useQuery } from "@tanstack/react-query";
import { useCurrencyService } from "@/services/CurrencyController";
import toast from "react-hot-toast";
import LoadingSpinner from "../general/LoadingSpinner";
import {NewOneTimeTransactionReq, NewRecurringTransactionReq} from "@/services/TransactionService";

const inputClass =
  "border-[1px] border-neutral-300 rounded-[5px] px-4 py-2 text-md w-full lg:min-w-76 ";

const defaultTransactionData: TransactionData = {
  name: "",
  amount: 0,
  currency: {
    currencyId: 1,
    isoCode: "PLN",
  },
  isIncome: true,
  description: "",
  frequency: "Raz",
  date: dayjs().format("YYYY-MM-DD"),
  startDate: dayjs().format("YYYY-MM-DD"),
  endDate: dayjs().format("YYYY-MM-DD"),
};

export default function TransactionForm({
  transaction,
  onCloseAction,
  header,
  submitButtonText,
  submitButtonIcon,
  onSubmitAction,
}: TransactionFormProps) {
  const [isStartDatePickerOpen, setIsStartDatePickerOpen] = useState(false);
  const [isEndDatePickerOpen, setIsEndDatePickerOpen] = useState(false);
  const [formData, setFormData] = useState<TransactionData>(transaction || defaultTransactionData);
  const formRef = useRef<HTMLDivElement>(null);
  const [amountInput, setAmountInput] = useState<string>(
      (transaction?.amount ?? 0).toString()
  );

  const CurrencyService = useCurrencyService();

  const { data: currencyList, isSuccess: isCurrencyListReady } = useQuery({
    queryKey: ["currencyData"],
    queryFn: CurrencyService.getCurrencyList,
  });

  const handleInputChange = (
      e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>
  ) => {
    const { name, value } = e.target;

    if (name === "amount") {
      setAmountInput(value);
      const parsed = parseFloat(value.replace(",", "."));
      if (!Number.isNaN(parsed)) {
        setFormData((prev) => ({ ...prev, amount: parsed }));
      }
      return;
    }

    if (name === "currency") {
      const selectedCurrency = currencyList.find(
          (currency) => currency.currencyId === Number(value)
      );
      if (selectedCurrency) {
        setFormData((prev) => ({ ...prev, currency: selectedCurrency }));
      }
      return;
    }

    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
    console.log(formData);
  };

  const handleTypeChange = (isIncome: boolean) => {
    setFormData({
      ...formData,
      isIncome: isIncome,
    });
  };

  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (
          formRef.current &&
          !formRef.current.contains(event.target as Node)
      ) {
        setIsStartDatePickerOpen(false);
        setIsEndDatePickerOpen(false);
      }
    };

    document.addEventListener("mousedown", handleClickOutside);
    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, []);

  function validateFormData(data: NewOneTimeTransactionReq | NewRecurringTransactionReq) {
    if (isNaN(data.amount)) {
      toast.error("Price is not a number!");
      return false;
    } else if (!data.amount) {
      toast.error("Price is empty!");
      return false;
    } else if (data.isIncome !== false && data.isIncome !== true) {
      toast.error("Type is not selected!" + JSON.stringify(data.isIncome));
      return false;
    } else if (!data.name || data.name.length < 3) {
      toast.error("Invalid name!");
      return false;
    }
    return true;
  }

  const handleSubmit = () => {
    if (!validateFormData(formData)) {
      return;
    }
    onCloseAction();
    onSubmitAction(formData);
  };

  return (
    <GenericPopup
      title={header}
      onCloseAction={onCloseAction}
      showConfirm={false} // We'll use our custom button instead
    >
      <div ref={formRef}>
        {/* Name */}
        <div className="py-1" >
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

        {formData.frequency !== "Raz" ? (
            <>
              <div className="py-1">
                <h3 className="text-md my-2 font-medium">Data początkowa</h3>
                <input
                    name="startDate"
                    className={inputClass}
                    type="text"
                    value={formData.startDate}
                    onChange={handleInputChange}
                    onClick={() => setIsStartDatePickerOpen((prev) => !prev)}
                />
                  <DatePicker
                      isOpen={isStartDatePickerOpen}
                      currentDate={formData.startDate}
                      setIsOpenAction={setIsStartDatePickerOpen}
                      setDateAction={(newDate) =>
                          setFormData((prev) => ({
                            ...prev,
                            startDate: dayjs(newDate).format("YYYY-MM-DD"),
                          }))
                      }
                  />
              </div>
              <div className="py-1">
                <h3 className="text-md my-2 font-medium">Data końcowa</h3>
                <input
                    name="endDate"
                    className={inputClass}
                    type="text"
                    value={formData.endDate}
                    onChange={handleInputChange}
                    onClick={() => setIsEndDatePickerOpen((prev) => !prev)}
                />
                  <DatePicker
                      isOpen={isEndDatePickerOpen}
                      currentDate={formData.endDate}
                      setIsOpenAction={setIsEndDatePickerOpen}
                      setDateAction={(newDate) =>
                          setFormData((prev) => ({
                            ...prev,
                            endDate: dayjs(newDate).format("YYYY-MM-DD"),
                          }))
                      }
                  />
              </div>
            </>
        ) : (
            <div className="py-1">
              <h3 className="text-md my-2 font-medium">Data</h3>
              <input
                  name="date"
                  className={inputClass}
                  type="text"
                  value={formData.date}
                  onChange={handleInputChange}
                  onClick={() => setIsStartDatePickerOpen((prev) => !prev)}
              />
              <DatePicker
                  isOpen={isStartDatePickerOpen}
                  currentDate={formData.date}
                  setIsOpenAction={setIsStartDatePickerOpen}
                  setDateAction={(newDate) =>
                      setFormData((prev) => ({
                        ...prev,
                        date: dayjs(newDate).format("YYYY-MM-DD"),
                      }))
                  }
              />
            </div>
        )}

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
        {isCurrencyListReady ? (
          <div className="flex gap-x-2 min-w-72">
            <input
              name="amount"
              className="border-[1px] border-neutral-300 rounded-[5px] px-4 py-2 text-md font-lato w-full"
              type="text"
              placeholder="Kwota"
              value={amountInput}
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
        ) : (
          <LoadingSpinner />
        )}

        <ConfirmButton
          icon={submitButtonIcon}
          variant="dark"
          className="mt-7"
          onClick={handleSubmit}
        >
          {submitButtonText}
        </ConfirmButton>
      </div>
    </GenericPopup>
  );
}