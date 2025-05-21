"use client";

import ThreeElementsCard from "@/components/dashboard/cards/generic/ThreeElementsCard";
import CardText from "@/components/dashboard/cards/generic/CardText";
import { useCardService } from "@/services/CardService";
import formatMoney from "@/utils/formatMoney";
import CardNumber from "@/components/dashboard/cards/generic/CardNumber";
import LoadingSpinner from "@/components/general/LoadingSpinner";
import dayjs from "dayjs";
import TextNumberField from "@/components/dashboard/cards/generic/TextNumberField";
import { useQueryWithToast } from "@/lib/data-grabbers";

export default function CurrentBalanceCard() {
  const CardService = useCardService();

  const { data, isLoading } = useQueryWithToast({
    queryKey: ["cardService", "getCurrentBalance"],
    queryFn: CardService.getCurrentBalance,
  });

  if (isLoading || !data) {
    return <LoadingSpinner />;
  }

  return (
    <ThreeElementsCard
      top={<CardText text="Aktualny stan konta" />}
      middle={<CardNumber text={formatMoney(data.currentBalance) + " zł"} />}
      bottom={
        <TextNumberField
          text={"Stan na dzień"}
          number={dayjs().format("DD-MM-YYYY")}
        />
      }
    />
  );
}
