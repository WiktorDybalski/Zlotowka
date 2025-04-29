"use client";

import ThreeElementsCard from "@/components/dashboard/cards/generic/ThreeElementsCard";
import CardText from "@/components/dashboard/cards/generic/CardText";
import { useCardService } from "@/services/CardService";
import { useEffect, useState } from "react";
import formatMoney from "@/utils/formatMoney";
import CardNumber from "@/components/dashboard/cards/generic/CardNumber";
import LoadingSpinner from "@/components/general/LoadingSpinner";
import toast from "react-hot-toast";
import dayjs from "dayjs";
import TextNumberField from "@/components/dashboard/cards/generic/TextNumberField";

export default function CurrentBalanceCard() {
  const [currentBalance, setCurrentBalance] = useState<number | null>(null);
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const CardService = useCardService();

  useEffect(() => {
    CardService.getCurrentBalance()
      .then((response) => {
        setCurrentBalance(response.currentBalance);
      })
      .catch((err) => {
        toast.error(err);
      })
      .finally(() => {
        setIsLoading(false);
      });
  }, []);

  if (isLoading || !currentBalance) {
    return <LoadingSpinner />;
  }

  return (
    <ThreeElementsCard
      top={<CardText text="Aktualny stan konta" />}
      middle={<CardNumber text={formatMoney(currentBalance) + " zł"} />}
      bottom={
        <TextNumberField
          text={"Stan na dzień"}
          number={dayjs().format("DD-MM-YYYY")}
        />
      }
    />
  );
}
