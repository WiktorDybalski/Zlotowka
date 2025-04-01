import {useEffect, useState} from "react";
import {CardsPopupProps} from "@/interfaces/dashboard/cards/CardPopupProps";
import {CardId} from "@/interfaces/dashboard/cards/CardComponents";
import DarkButton from "@/components/DarkButton";

const formatCardLabel = (card: string) => {
  const formatted = card.replace(/([A-Z])/g, " $1").trim();
  return formatted.charAt(0).toUpperCase() + formatted.slice(1).toLowerCase();
};

// TODO: change availableCards
export default function CardsPopup({
                                     setSelectedCards,
                                     onClose,
                                     selectedCards,
                                   }: CardsPopupProps) {
  const [isVisible, setIsVisible] = useState(false);
  const [localSelectedCards, setLocalSelectedCards] = useState<CardId[]>([
    ...selectedCards,
  ]);

  const availableCards: CardId[] = ["nextExpense", "pinnedDream", "monthForecast", "currentBalance"];

  const handleClose = () => {
    setIsVisible(false);
    setTimeout(() => onClose(), 300);
  };

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
      alert("Musisz wybrać dokładnie trzy karty!");
      return;
    }
    setSelectedCards(localSelectedCards);
    onClose();
  };

  useEffect(() => {
    const timeout = setTimeout(() => {
      setIsVisible(true);
    }, 50);

    return () => clearTimeout(timeout);
  }, []);

  return (
      <div
          className={`w-full h-screen fixed top-0 left-0 flex justify-center items-center select-none transition-opacity duration-200 z-[1000] 
        ${isVisible ? "opacity-100" : "opacity-0"}`}
      >
        <div
            className="absolute w-full h-full bg-[#818181] opacity-70 transition-opacity duration-300 z-[999]"
            onClick={handleClose}
        ></div>
        <div
            className={`bg-neutral-100 border-[1px] border-[rgba(38,38,38,0.5)] p-6 rounded-[10px] z-[1001] 
          transition-all duration-200 ease-in-out transform ${isVisible ? "scale-100 opacity-100" : "scale-90 opacity-0"}`}
        >
          <h2 className="text-xl font-semibold mb-6">Wybierz karty do wyświetlenia</h2>
          <div className="space-y-2">
            {availableCards.map((card) => (
                <div key={card} className="flex items-center space-x-2">
                  <input
                      type="checkbox"
                      id={card}
                      checked={localSelectedCards.includes(card)}
                      onChange={() => handleCardToggle(card)}
                      className="h-5 w-5"
                  />
                  <label htmlFor={card} className="text-sm">
                    {formatCardLabel(card)}
                  </label>
                </div>
            ))}
          </div>

          <div className="mt-6 flex justify-end space-x-4">
            <DarkButton text={"Zatwierdź"} onClick={handleConfirm} />
          </div>
        </div>
      </div>
  );
}
