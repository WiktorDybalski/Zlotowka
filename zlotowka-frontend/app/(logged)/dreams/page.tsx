"use client";

import DarkButton from "@/components/DarkButton";
import AddDreamComponentPopup from "@/components/dreams/AddDreamPopUp";
import DreamCard from "@/components/dreams/DreamCard";
import { NewDreamReq, useDreamsService } from "@/services/DreamsService";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { useState } from "react";
import toast from "react-hot-toast";
import { useQueryWithToast } from "@/lib/data-grabbers";

export default function Dreams() {
  const DreamService = useDreamsService();
  const queryClient = useQueryClient();
  const [showPopup, setShowPopup] = useState(false);

  const { data, error } = useQueryWithToast({
    queryKey: ["dreams", "getAllDreams"],
    queryFn: DreamService.getAllDreams,
  });

  const newDreamMutation = useMutation({
    mutationFn: async (dream: NewDreamReq) => {
      const res = DreamService.addDream(dream);
      toast.promise(res, {
        loading: "Dodawanie marzenia...",
        success: "Marzenie dodane pomyślnie!",
        error: (error) =>
          `Wystąpił błąd podczas dodawania marzenia: ${error.message}`,
      });
      return await res;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["dreams"] });
    },
  });

  return (
    <>
      {showPopup && (
        <AddDreamComponentPopup
          onClose={() => {
            setShowPopup(false);
          }}
          onSubmit={(data) => {
            newDreamMutation.mutate({
              name: data.componentName,
              description: data.description,
              amount: data.amount,
              currencyId: data.currency.currencyId,
            });
            setShowPopup(false);
          }}
        />
      )}
      {error && (
        <div className="w-full h-full flex justify-center items-center">
          <h1 className="text-2xl font-bold text-red-500">{error.message}</h1>
        </div>
      )}
      <div className="flex flex-col sm:flex-row items-center justify-between gap-y-4 sm:gap-y-0 gap-x-10">
        <h2 className="text-3xl md:text-4xl lg:text-5xl font-bold">
          Twoje Marzenia
        </h2>
        <div className="w-full sm:w-52 h-10 sm:mt-5">
          <DarkButton
            icon={"add"}
            text={"Dodaj marzenie"}
            onClick={() => {
              setShowPopup(true);
            }}
          />
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 3xl:grid-cols-3 gap-5 mt-5 sm:mt-10">
        {data &&
          data.map((dream, idx) => <DreamCard key={idx} dream={dream} />)}
      </div>
    </>
  );
}
