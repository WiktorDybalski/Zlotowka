import CardText from "@/components/dashboard/cards/generic/CardText";
import ThreeElementsCard from "@/components/dashboard/cards/generic/ThreeElementsCard";

const Value = () => (
  <div className="flex items-baseline font-lato">
    <p>59 000.50zł</p>
    <p className="text-base font-medium ml-3 text-red-700">(-40%)</p>
  </div>
)

export default function MonthForecastCard() {
  return (
      <ThreeElementsCard
          top={<CardText text="Prognoza finansowa" />}
          middle={<Value />}
          bottom={<CardText text="Szacowane saldo na koniec miesiąca" />}
      />
  )
}