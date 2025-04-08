"use client"

import ThreeElementsCard from "@/components/dashboard/cards/generic/ThreeElementsCard";
import CardText from "@/components/dashboard/cards/generic/CardText";
import CardNumber from "@/components/dashboard/cards/generic/CardNumber";
import {useEffect, useState} from "react";
import CardService from "@/services/CardService";
import formatMoney from "@/utils/formatMoney";
import {NextTransactionResponse} from "@/interfaces/dashboard/cards/NextTransactionResponse";
import DateInfo from "@/components/dashboard/cards/generic/DateInfo";


export default function NextExpenseCard() {
  const [nextExpense, setNextExpense] = useState<NextTransactionResponse | null>(null);

  useEffect(() => {
    CardService.getNextTransaction(1, false).then(response => {
      setNextExpense(response);
    });
  }, []);

  if (nextExpense === null) {
    return null;
  }

  return (
      <ThreeElementsCard
          top={<CardText text="Następny wydatek" />}
          middle={<CardNumber text={formatMoney(nextExpense.amount) + " " + nextExpense.currencyIsoCode} />}
          bottom={<DateInfo text={nextExpense.transactionName} date={nextExpense.date} />}
      />
  );
}