"use client";

import { useState } from "react";
import { CardsPopupProps } from "@/interfaces/dashboard/cards/CardPopupProps";
import {
  AVAILABLE_CARDS,
  CardId,
} from "@/interfaces/dashboard/cards/CardComponents";
import GenericPopup from "@/components/general/GenericPopup";
import toast from "react-hot-toast";

export default function CardsPopup({
                                     setSelectedCards,
                                     onClose,
                                     selectedCards,
                                   }: CardsPopupProps) {
  const [localSelectedCards, setLocalSelectedCards] = useState<CardId[]>([
    ...selectedCards,
  ]);

  const handleCardToggle = (card: CardId) => {
    setLocalSelectedCards((prevSelected: CardId[]) => {
      if (prevSelected.includes(card)) {
        return prevSelected.filter((item) => item !== card);
      }
      return [...prevSelected, card];
    });
  };

  const handleConfirm = () => {
    if (localSelectedCards.length !== 3) {
      toast.error("Musisz wybrać dokładnie 3 karty");
      return;
    }
    setSelectedCards(localSelectedCards);
    onClose();
  };

  return (
      <GenericPopup
          title="Wybierz dokładnie trzy karty"
          onCloseAction={onClose}
          onConfirmAction={handleConfirm}
          confirmText="Zatwierdź"
      >
        <div className="space-y-4">
          {AVAILABLE_CARDS.map((card) => (
              <div
                  key={card.id}
                  className="flex items-center p-2 rounded hover:bg-neutral-200 transition-colors"
              >
                <input
                    type="checkbox"
                    id={card.id}
                    checked={localSelectedCards.includes(card.id)}
                    onChange={() => handleCardToggle(card.id)}
                    className="h-5 w-5 accent-accent"
                />
                <label htmlFor={card.id} className="text-sm ml-4">
                  {card.label}
                </label>
              </div>
          ))}
        </div>
      </GenericPopup>
  );
}