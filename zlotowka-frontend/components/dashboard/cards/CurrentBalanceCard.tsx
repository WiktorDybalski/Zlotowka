"use client"

import ThreeElementsCard from "@/components/dashboard/cards/generic/ThreeElementsCard";
import CardText from "@/components/dashboard/cards/generic/CardText";
import CardService from "@/services/CardService";
import {useEffect, useState} from "react";
import formatMoney from "@/utils/formatMoney";
import CardNumber from "@/components/dashboard/cards/generic/CardNumber";

export default function CurrentBalanceCard() {
  const [currentBalance, setCurrentBalance] = useState<number | null>(null);

  useEffect(() => {
    CardService.getCurrentBalance(1).then(setCurrentBalance);
  }, []);

  if (!currentBalance) {
    return null;
  }

  return (
      <ThreeElementsCard
          top={<CardText text="Aktualny stan konta" />}
          middle={<CardNumber text={formatMoney(currentBalance) + " zł"} />}
          bottom={<CardText text="No jakiś fancy tekst" />}
      />
  )
}