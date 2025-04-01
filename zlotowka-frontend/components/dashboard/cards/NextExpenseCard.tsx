import ThreeElementsCard from "@/components/dashboard/cards/ThreeElementsCard";
import CardText from "@/components/dashboard/cards/CardText";
import CardNumber from "@/components/dashboard/cards/CardNumber";

const DateInfo = () => (
    <div className="w-full flex justify-between">
      <CardText text="Zakupy spożywcze" />
      <CardNumber text="28.03.2025" />
    </div>
)

export default function NextExpenseCard() {
  return (
      <ThreeElementsCard
          top={<CardText text="Następny wydatek" />}
          middle={<CardNumber text="29 500.50zł" />}
          bottom={<DateInfo />}
      />
  );
}