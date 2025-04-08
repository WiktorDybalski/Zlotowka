import {CardId} from "@/interfaces/dashboard/cards/CardComponents";

export interface CardsPopupProps {
  setSelectedCards: (cards: CardId[]) => void;
  onClose: () => void;
  selectedCards: CardId[];
}