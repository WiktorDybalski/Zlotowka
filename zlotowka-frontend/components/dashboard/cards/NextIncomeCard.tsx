"use client"

import ThreeElementsCard from "@/components/dashboard/cards/generic/ThreeElementsCard";
import CardText from "@/components/dashboard/cards/generic/CardText";
import CardNumber from "@/components/dashboard/cards/generic/CardNumber";
import {useEffect, useState} from "react";
import CardService from "@/services/CardService";
import formatMoney from "@/utils/formatMoney";
import {NextTransactionResponse} from "@/interfaces/dashboard/cards/NextTransactionResponse";
import DateInfo from "@/components/dashboard/cards/generic/DateInfo";

export default function NextIncomeCard() {
  const [nextIncome, setNextIncome] = useState<NextTransactionResponse | null>(null);

  useEffect(() => {
    CardService.getNextTransaction(1, true).then(response => {
      setNextIncome(response);
    });
  }, []);

  if (nextIncome === null) {
    return null;
  }

  return (
      <ThreeElementsCard
          top={<CardText text="Następny przychód" />}
          middle={<CardNumber text={formatMoney(nextIncome.amount) + " " + nextIncome.currencyIsoCode} />}
          bottom={<DateInfo text={nextIncome.transactionName} date={nextIncome.date} />}
      />
  );
}