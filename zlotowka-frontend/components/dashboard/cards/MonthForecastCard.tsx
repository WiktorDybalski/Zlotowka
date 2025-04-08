"use client"

import CardText from "@/components/dashboard/cards/generic/CardText";
import ThreeElementsCard from "@/components/dashboard/cards/generic/ThreeElementsCard";
import {useEffect, useState} from "react";
import CardService from "@/services/CardService";
import formatMoney from "@/utils/formatMoney";
import CardNumber from "@/components/dashboard/cards/generic/CardNumber";
import toast from "react-hot-toast";
import LoadingSpinner from "@/components/general/LoadingSpinner";

const Value = ({ estimatedBalance }: { estimatedBalance: string }) => (
  <div className="flex items-baseline">
    <CardNumber text={estimatedBalance} />
    <CardNumber text="(-40%)" className="text-base font-medium ml-3 text-red-700"/>
  </div>
)

export default function MonthForecastCard() {
  const [estimatedBalance, setEstimatedBalance] = useState<number | null>(null);
  const [isLoading, setIsLoading] = useState<boolean>(true);

  useEffect(() => {
    CardService.getMonthEstimatedBalance(1)
        .then(response => {
          setEstimatedBalance(response.estimatedBalance);
        }).catch(err => {
          toast.error("Failed to fetch estimated balance: " + err);
        })
        .finally(() => {
           setIsLoading(false);
        });
  }, []);

  if (isLoading || !estimatedBalance) {
    return <LoadingSpinner/>;
  }

  return (
      <ThreeElementsCard
          top={<CardText text="Prognoza finansowa" />}
          middle={<Value estimatedBalance={formatMoney(estimatedBalance) + " zł"} />}
          bottom={<CardText text="Szacowane saldo na koniec miesiąca" />}
      />
  )
}