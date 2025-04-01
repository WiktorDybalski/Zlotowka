import CardText from "@/components/dashboard/cards/CardText";
import ThreeElementsCard from "@/components/dashboard/cards/ThreeElementsCard";
import CardNumber from "@/components/dashboard/cards/CardNumber";

const Progress = () => (
    <div className="flex flex-col">
      <p className="text-xl">Wakacje na malediwach</p>
      <div className="w-full h-5 my-2 bg-neutral-200 rounded-xl"></div>
    </div>
)

const GoalInfo = () => (
    <div className="w-full flex justify-between shrink">
      <CardText text="Pełna kwota" />
      <CardNumber text="3 000 zł" />
    </div>
)

export default function PinnedDreamCard() {
  return (
      <ThreeElementsCard
          top={<CardText text="Twój wybrany cel" />}
          middle={<Progress />}
          bottom={<GoalInfo />}
      />
  )
}