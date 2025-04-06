"use client"

import ThreeElementsCard from "@/components/dashboard/cards/generic/ThreeElementsCard";
import CardText from "@/components/dashboard/cards/generic/CardText";
import CardNumber from "@/components/dashboard/cards/generic/CardNumber";
import {useEffect, useState} from "react";
import CardService from "@/services/CardService";
import formatMoney from "@/utils/formatMoney";

const DateInfo = ({ text, date }: { text : string, date : string} ) => (
    <div className="w-full flex justify-between">
      <CardText text={text} />
      <CardNumber text={date} />
    </div>
)

interface NextTransactionResponse {
  value: number;
  name: string;
  date: string;
}

export default function NextTransactionCard() {
  const [nextTransaction, setNextTransaction] = useState<NextTransactionResponse | null>(null);

  useEffect(() => {
    CardService.getNextTransaction(1).then(setNextTransaction);
  }, []);

  if (!nextTransaction) {
    return null;
  }

  return (
      <ThreeElementsCard
          top={<CardText text="Następna transakcja" />}
          middle={<CardNumber text={formatMoney(nextTransaction.value) + " zł"} />}
          bottom={<DateInfo text={nextTransaction.name} date={nextTransaction.date} />}
      />
  );
}