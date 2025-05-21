"use client";

import CardText from "@/components/dashboard/cards/generic/CardText";
import ThreeElementsCard from "@/components/dashboard/cards/generic/ThreeElementsCard";
import { useCardService } from "@/services/CardService";
import formatMoney from "@/utils/formatMoney";
import CardNumber from "@/components/dashboard/cards/generic/CardNumber";
import LoadingSpinner from "@/components/general/LoadingSpinner";
import { useQueryWithToast } from "@/lib/data-grabbers";

const Value = ({ estimatedBalance }: { estimatedBalance: string }) => (
  <div className="flex items-baseline">
    <CardNumber text={estimatedBalance} />
  </div>
);

export default function MonthForecastCard() {
  const CardService = useCardService();

  const { data, isLoading } = useQueryWithToast({
    queryKey: ["cardService", "getMonthEstimatedBalance"],
    queryFn: CardService.getMonthEstimatedBalance,
  });

  if (isLoading || !data) {
    return <LoadingSpinner />;
  }

  return (
    <ThreeElementsCard
      top={<CardText text="Prognoza finansowa" />}
      middle={
        <Value estimatedBalance={formatMoney(data.estimatedBalance) + " zł"} />
      }
      bottom={<CardText text="Szacowane saldo na koniec miesiąca" />}
    />
  );
}
