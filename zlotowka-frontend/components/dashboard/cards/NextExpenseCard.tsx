"use client";

import ThreeElementsCard from "@/components/dashboard/cards/generic/ThreeElementsCard";
import CardText from "@/components/dashboard/cards/generic/CardText";
import CardNumber from "@/components/dashboard/cards/generic/CardNumber";
import { useCardService } from "@/services/CardService";
import formatMoney from "@/utils/formatMoney";
import TextNumberField from "@/components/dashboard/cards/generic/TextNumberField";
import toast from "react-hot-toast";
import LoadingSpinner from "@/components/general/LoadingSpinner";
import dayjs from "dayjs";
import { useQuery } from "@tanstack/react-query";

export default function NextExpenseCard() {
  const CardService = useCardService();

  const { data, isLoading, isError, error } = useQuery({
    queryKey: ["cardService", "getNextExpense"],
    queryFn: () => CardService.getNextTransaction(false),
  });

  if (isError) {
    toast.error(error?.message || "Błąd podczas pobierania następnego wydatku");
  }

  if (isLoading || !data) {
    return <LoadingSpinner />;
  }

  return (
    <ThreeElementsCard
      top={<CardText text="Następny wydatek" />}
      middle={
        <CardNumber
          text={formatMoney(data.amount) + " " + data.currencyIsoCode}
        />
      }
      bottom={
        <TextNumberField
          text={data.transactionName}
          number={dayjs(data.date).format("DD-MM-YYYY")}
        />
      }
    />
  );
}
