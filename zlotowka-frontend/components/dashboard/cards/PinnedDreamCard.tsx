"use state"
import CardText from "@/components/dashboard/cards/generic/CardText";
import ThreeElementsCard from "@/components/dashboard/cards/generic/ThreeElementsCard";
import TextNumberField from "@/components/dashboard/cards/generic/TextNumberField";
import { useDreamContext} from "@/components/dreams/DreamsContext";
import {ProgressBar} from "@/components/general/ProgressBar";
import {useQuery} from "@tanstack/react-query";
import {Dream, useDreamsService} from "@/services/DreamsService";
import LoadingSpinner from "@/components/general/LoadingSpinner";
import {useEffect, useState} from "react";

const Progress =  ({ dream }: { dream: Dream }) => (
  <div className="flex flex-col">
    <p className="text-lg xl:text-xl mb-1">{dream.name}</p>
    <ProgressBar progress={dream.actualAmount / dream.amount} />
  </div>
);

export default function PinnedDreamCard() {
  const { pickedDream } = useDreamContext();
  const DreamService = useDreamsService();
  const [dream, setDream] = useState<Dream | undefined>(undefined);

  const { data, isLoading } = useQuery({
    queryKey: ["dreams", "getAllDreams"],
    queryFn: DreamService.getAllDreams,
  });

  useEffect(() => {
    if (data && pickedDream !== null) {
      const found = data.find(d => d.planId === pickedDream);
      setDream(found);
    }
  }, [data, pickedDream]);

  if (!dream) {
    return <ThreeElementsCard
        top={<CardText text="Twój wybrany cel" />}
        middle={<p className="text-sm">Nie masz żadnych wybranych marzeń</p>}
        bottom={<div></div>}
    />
  }

  if (isLoading) {
    return <LoadingSpinner />
  }


  return (
        <ThreeElementsCard
          top={<CardText text="Twój wybrany cel" />}
          middle={<Progress dream={dream} />}
          bottom={<TextNumberField text={"Pełna kwota"} number={dream.amount.toString() + " " + dream.currency.isoCode} />}
        />
  );
}
