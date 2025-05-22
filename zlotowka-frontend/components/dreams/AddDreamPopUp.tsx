"use client";

import { useState } from "react";
import GenericPopup from "@/components/general/GenericPopup";
import DarkButton from "@/components/DarkButton";
import { useCurrencyService } from "@/services/CurrencyController";
import LoadingSpinner from "@/components/general/LoadingSpinner";
import toast from "react-hot-toast";
import { useQueryWithToast } from "@/lib/data-grabbers";

export interface DreamComponentData {
  componentName: string;
  description: string;
  amount: number;
  currency: {
    currencyId: number;
    isoCode: string;
  };
}

const defaultDreamComponentData: DreamComponentData = {
  componentName: "",
  description: "",
  amount: 0,
  currency: {
    currencyId: 1,
    isoCode: "PLN",
  },
};

interface AddDreamComponentPopupProps {
  onSubmit: (data: DreamComponentData) => void;
  onClose: () => void;
  providedDream?: DreamComponentData;
  windowTitle?: string;
  submitButtonText?: string;
}

export default function AddDreamComponentPopup({
  onSubmit,
  onClose,
  providedDream = defaultDreamComponentData,
  windowTitle = "Dodaj nowe marzenie",
  submitButtonText = "Dodaj marzenie",
}: AddDreamComponentPopupProps) {
  const [formData, setFormData] = useState<DreamComponentData>(providedDream);
  const CurrencyService = useCurrencyService();
  const { data: currencyList, isSuccess: isCurrencyListReady } =
    useQueryWithToast({
      queryKey: ["currencyData"],
      queryFn: CurrencyService.getCurrencyList,
    });

  const handleInputChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>
  ) => {
    const { name, value } = e.target;
    if (name === "amount") {
      if (value === "") {
        setFormData((prevState) => ({
          ...prevState,
          amount: 0,
        }));
        return;
      }
      const valueWithDot = value.replace(",", ".");
      const parsedValue = parseFloat(valueWithDot);
      if (!Number.isNaN(parsedValue)) {
        setFormData((prevState) => ({
          ...prevState,
          amount: parsedValue,
        }));
      }
    } else if (name === "currency") {
      const selectedCurrency = currencyList.find(
        (currency) => currency.currencyId === Number(value)
      );
      if (selectedCurrency) {
        setFormData((prevState) => ({
          ...prevState,
          currency: selectedCurrency,
        }));
      }
    } else {
      setFormData((prevState) => ({
        ...prevState,
        [name]: value,
      }));
    }
  };

  const validateFormData = (data: DreamComponentData) => {
    if (!data.componentName || data.componentName.trim().length < 3) {
      toast.error("Nazwa składowej musi mieć przynajmniej 3 znaki!");
      return false;
    }
    if (isNaN(data.amount) || data.amount <= 0) {
      toast.error("Kwota musi być większa niż 0!");
      return false;
    }
    return true;
  };

  const handleSubmit = () => {
    if (!validateFormData(formData)) {
      return;
    }
    onClose();
    onSubmit(formData);
  };

  return (
    <GenericPopup
      title={windowTitle}
      onCloseAction={onClose}
      showConfirm={false}
    >
      <section className="min-w-[360px]">
        <div className="py-1">
          <h3 className="text-md my-2 font-medium">Nazwa marzenia</h3>
          <input
            name="componentName"
            type="text"
            placeholder="Podaj nazwę"
            value={formData.componentName}
            onChange={handleInputChange}
            className="border border-neutral-300 rounded px-4 py-2 w-full"
          />
        </div>
        <div className="py-1">
          <h3 className="text-md my-2 font-medium">Opis</h3>
          <input
            name="description"
            type="text"
            placeholder="Opisz marzenie"
            value={formData.description}
            onChange={handleInputChange}
            className="border border-neutral-300 rounded px-4 py-2 w-full"
          />
        </div>
        <div className="flex gap-x-2 my-2">
          <input
            name="amount"
            type="text"
            placeholder="Kwota"
            value={formData.amount}
            onChange={handleInputChange}
            className="border border-neutral-300 rounded px-4 py-2 w-full font-lato"
          />
          {isCurrencyListReady ? (
            <select
              name="currency"
              value={formData.currency.currencyId}
              onChange={handleInputChange}
              className="border border-neutral-300 rounded px-4 py-2 w-32 bg-background"
            >
              {currencyList.map((currency) => (
                <option key={currency.currencyId} value={currency.currencyId}>
                  {currency.isoCode}
                </option>
              ))}
            </select>
          ) : (
            <LoadingSpinner minH={10} size={5} />
          )}
        </div>
        <div className="mt-7">
          <DarkButton
            icon="add"
            text={submitButtonText}
            onClick={handleSubmit}
            disabled={!isCurrencyListReady}
          />
        </div>
      </section>
    </GenericPopup>
  );
}
