"use client";

import ThreeElementsCard from "@/components/dashboard/cards/generic/ThreeElementsCard";
import CardText from "@/components/dashboard/cards/generic/CardText";
import CardNumber from "@/components/dashboard/cards/generic/CardNumber";
import { useEffect, useState } from "react";
import CardService from "@/services/CardService";
import formatMoney from "@/utils/formatMoney";
import { NextTransactionResponse } from "@/interfaces/dashboard/cards/NextTransactionResponse";
import TextNumberField from "@/components/dashboard/cards/generic/TextNumberField";
import toast from "react-hot-toast";
import LoadingSpinner from "@/components/general/LoadingSpinner";
import dayjs from "dayjs";

export default function NextExpenseCard() {
  const [nextExpense, setNextExpense] =
    useState<NextTransactionResponse | null>(null);
  const [isLoading, setIsLoading] = useState<boolean>(true);

  useEffect(() => {
    CardService.getNextTransaction(1, false)
      .then((response) => {
        setNextExpense(response);
      })
      .catch((err) => {
        toast.error("Failed to fetch next expense: " + err);
      })
      .finally(() => {
        setIsLoading(false);
      });
  }, []);

  if (isLoading || !nextExpense) {
    return <LoadingSpinner />;
  }

  return (
    <ThreeElementsCard
      top={<CardText text="Następny wydatek" />}
      middle={
        <CardNumber
          text={
            formatMoney(nextExpense.amount) + " " + nextExpense.currencyIsoCode
          }
        />
      }
      bottom={
        <TextNumberField
          text={nextExpense.transactionName}
          number={dayjs(nextExpense.date).format("DD-MM-YYYY")}
        />
      }
    />
  );
}
