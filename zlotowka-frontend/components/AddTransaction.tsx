"use client"

import { useState, useEffect } from "react";

const inputClass = "border-[1px] border-neutral-300 rounded-[5px] px-4 py-2 text-md min-w-76";


// TODO: datepicker, decomposition styles, select background, radio colors
export default function AddTransaction({ setShowAddTransaction }: { setShowAddTransaction: React.Dispatch<React.SetStateAction<boolean>> }) {
  const [selectedCurrency, setSelectedCurrency] = useState<string>("PLN");
  const [selectedFrequency, setSelectedFrequency] = useState<string>("Raz");
  const [isVisible, setIsVisible] = useState(false);

  const handleCurrencyChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    setSelectedCurrency(e.target.value);
  };

  const handleFrequencyChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    setSelectedFrequency(e.target.value);
  };

  const handleClose = () => {
    setIsVisible(false);
    setTimeout(() => setShowAddTransaction(false), 300);
  };

  useEffect(() => {
      setIsVisible(true);
  }, [setShowAddTransaction]);

  return (
      <div className={`w-full h-screen fixed top-0 left-0 flex justify-center items-center select-none transition-opacity duration-200 ${isVisible ? 'opacity-100' : 'opacity-0'}`}>
        {/* Background */}
        <div className="absolute w-full h-full bg-[#818181] opacity-70 transition-opacity duration-300" onClick={handleClose}></div>

        {/* Form */}
        <div className={`bg-neutral-100 border-[1px] border-[rgba(38,38,38,0.5)] px-8 py-10 rounded-[10px] z-10 transition-all duration-200 ease-in-out transform ${isVisible ? 'scale-100 opacity-100' : 'scale-90 opacity-0'}`}>
          <div className="mb-6">
            <h2 className="text-2xl font-medium">Dodaj transakcje</h2>
          </div>

          {/* Name */}
          <div className="py-1">
            <h3 className="text-md my-2 font-medium">Nazwa</h3>
            <input className={inputClass} type="text" placeholder="Zakupy spożywcze"/>
          </div>

          {/* Date picker */}
          <div className="py-1">
            <h3 className="text-md my-2 font-medium">Data</h3>
            <input className={inputClass} type="text" placeholder="Wyobraźcie sobie date pickera"/>
          </div>

          {/* Frequency select */}
          <div className="py-1">
            <h3 className="text-md my-2 font-medium">Cykliczność</h3>
            <select value={selectedFrequency} onChange={handleFrequencyChange} className={inputClass}>
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
                <input type="radio"/>
              </label>
              <h3>Przychód</h3>
            </div>
            <div className="flex gap-x-2">
              <label>
                <input type="radio" />
              </label>
              <h3>Wydatek</h3>
            </div>
          </div>

          {/* Price and currency */}
          <div className="flex gap-x-2 min-w-72">
            <input className="border-[1px] border-neutral-300 rounded-[5px] px-4 py-2 text-md w-full" type="text" placeholder="Kwota" />
            <select value={selectedCurrency} onChange={handleCurrencyChange} className="border-[1px] border-neutral-300 rounded-[5px] px-2 text-md">
              <option value="PLN">PLN</option>
              <option value="EUR">EUR</option>
              <option value="USD">USD</option>
            </select>
          </div>

          {/* Button */}
          <div className="mt-7 w-full">
            <button className="bg-[#262626] rounded-[10px] flex justify-center items-center w-full text-neutral-100 py-2 text-sm gap-x-2 hover:cursor-pointer">
              <span className="material-symbols-outlined">add</span>
              <h3 className="">Dodaj transakcję</h3>
            </button>
          </div>
        </div>
      </div>
  );
}
