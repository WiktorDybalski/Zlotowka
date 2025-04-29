"use client";

import ThreeElementsCard from "@/components/dashboard/cards/generic/ThreeElementsCard";
import CardText from "@/components/dashboard/cards/generic/CardText";
import CardNumber from "@/components/dashboard/cards/generic/CardNumber";
import { useEffect, useState } from "react";
import { useCardService } from "@/services/CardService";
import formatMoney from "@/utils/formatMoney";
import { NextTransactionResponse } from "@/interfaces/dashboard/cards/NextTransactionResponse";
import TextNumberField from "@/components/dashboard/cards/generic/TextNumberField";
import toast from "react-hot-toast";
import LoadingSpinner from "@/components/general/LoadingSpinner";
import dayjs from "dayjs";

export default function NextIncomeCard() {
  const [nextIncome, setNextIncome] = useState<NextTransactionResponse | null>(
    null
  );
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const CardService = useCardService();

  useEffect(() => {
    CardService.getNextTransaction(true)
      .then((response) => {
        setNextIncome(response);
      })
      .catch((err) => {
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
      middle={
        <CardNumber
          text={
            formatMoney(nextIncome.amount) + " " + nextIncome.currencyIsoCode
          }
        />
      }
      bottom={
        <TextNumberField
          text={nextIncome.transactionName}
          number={dayjs(nextIncome.date).format("DD-MM-YYYY")}
        />
      }
    />
  );
}
