import {useEffect, useState} from "react";
import {CardsPopupProps} from "@/interfaces/dashboard/cards/CardPopupProps";
import {AVAILABLE_CARDS, CardId} from "@/interfaces/dashboard/cards/CardComponents";
import DarkButton from "@/components/DarkButton";
import toast from "react-hot-toast";

export default function CardsPopup({
                                     setSelectedCards,
                                     onClose,
                                     selectedCards,
                                   }: CardsPopupProps) {
  const [isVisible, setIsVisible] = useState(false);
  const [localSelectedCards, setLocalSelectedCards] = useState<CardId[]>([
    ...selectedCards,
  ]);

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
      toast.error("Musisz wybrać dokładnie 3 karty");
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
          <div className="space-y-4">
            {AVAILABLE_CARDS.map((card) => (
                <div key={card.id} className="flex items-center p-2 rounded hover:bg-neutral-200 transition-colors">
                  <input
                      type="checkbox"
                      id={card.id}
                      checked={localSelectedCards.includes(card.id)}
                      onChange={() => handleCardToggle(card.id)}
                      className="h-5 w-5 accent-neutral-800"
                  />
                  <label htmlFor={card.id} className="text-sm ml-4">
                    {card.label}
                  </label>
                </div>
            ))}
          </div>
          <div className="mt-6 flex justify-between space-x-4">
            <DarkButton text={"Zatwierdź"} onClick={handleConfirm} />
          </div>
        </div>
      </div>
  );
}
