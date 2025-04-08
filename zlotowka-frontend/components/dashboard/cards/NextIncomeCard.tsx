"use client"

import ThreeElementsCard from "@/components/dashboard/cards/generic/ThreeElementsCard";
import CardText from "@/components/dashboard/cards/generic/CardText";
import CardNumber from "@/components/dashboard/cards/generic/CardNumber";
import {useEffect, useState} from "react";
import CardService from "@/services/CardService";
import formatMoney from "@/utils/formatMoney";
import {NextTransactionResponse} from "@/interfaces/dashboard/cards/NextTransactionResponse";
import DateInfo from "@/components/dashboard/cards/generic/DateInfo";
import toast from "react-hot-toast";
import LoadingSpinner from "@/components/general/LoadingSpinner";

export default function NextIncomeCard() {
  const [nextIncome, setNextIncome] = useState<NextTransactionResponse | null>(null);
  const [isLoading, setIsLoading] = useState<boolean>(true);

  useEffect(() => {
    CardService.getNextTransaction(1, false)
        .then(response => {
          setNextIncome(response);
        })
        .catch(err => {
          toast.error("Failed to fetch next income: " + err);
        })
        .finally(() => {
          setIsLoading(false);
        });
  }, []);

  if (isLoading || !nextIncome) {
    return <LoadingSpinner />;
  }

  return (
      <ThreeElementsCard
          top={<CardText text="Następny przychód" />}
          middle={<CardNumber text={formatMoney(nextIncome.amount) + " " + nextIncome.currencyIsoCode} />}
          bottom={<DateInfo text={nextIncome.transactionName} date={nextIncome.date} />}
      />
  );
}