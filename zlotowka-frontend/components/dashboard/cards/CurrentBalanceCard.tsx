import ThreeElementsCard from "@/components/dashboard/cards/ThreeElementsCard";
import CardText from "@/components/dashboard/cards/CardText";

export default function CurrentBalanceCard() {
  return (
      <ThreeElementsCard
          top={<CardText text="Aktualny stan konta" />}
          middle={<CardText text="60 000.70zł" />}
          bottom={<CardText text="No jakiś fancy tekst" />}
      />
  )
}