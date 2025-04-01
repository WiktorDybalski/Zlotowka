import ThreeElementsCard from "@/components/dashboard/cards/generic/ThreeElementsCard";
import CardText from "@/components/dashboard/cards/generic/CardText";
import CardNumber from "@/components/dashboard/cards/generic/CardNumber";

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