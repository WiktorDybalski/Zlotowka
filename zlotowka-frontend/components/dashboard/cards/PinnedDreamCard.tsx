import CardText from "@/components/dashboard/cards/generic/CardText";
import ThreeElementsCard from "@/components/dashboard/cards/generic/ThreeElementsCard";
import TextNumberField from "@/components/dashboard/cards/generic/TextNumberField";

const Progress = () => (
  <div className="flex flex-col">
    <p className="text-lg xl:text-xl">Wakacje na malediwach</p>
    <div className="w-full h-5 my-2 bg-neutral-200 rounded-xl"></div>
  </div>
);

export default function PinnedDreamCard() {
  return (
    <ThreeElementsCard
      top={<CardText text="Twój wybrany cel" />}
      middle={<Progress />}
      bottom={<TextNumberField text={"Pełna kwota"} number={"3000zl"} />}
    />
  );
}
