"use client";

import DarkButton from "@/components/DarkButton";
import AddSubDreamComponentPopup from "@/components/dreams/AddSubDreamPopUp";
import SubDreamCard from "@/components/dreams/SubDreamCard";
import LoadingSpinner from "@/components/general/LoadingSpinner";
import { ProgressBar } from "@/components/general/ProgressBar";
import routes from "@/routes";
import { NewSubDreamReq, useDreamsService } from "@/services/DreamsService";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { redirect, useParams, useRouter } from "next/navigation";
import { useEffect, useState } from "react";
import toast from "react-hot-toast";

export default function DreamDetailsPage() {
  const { planId } = useParams();
  const DreamService = useDreamsService();
  const numericPlanId = Number(planId);

  const [showPopup, setShowPopup] = useState(false);
  const queryClient = useQueryClient();
  const router = useRouter();

  useEffect(() => {
    if (isNaN(numericPlanId)) {
      redirect(routes.dreams.pathname); // redirect to the dreams page if planId is not valid
    }
  }, [numericPlanId]);

  const {
    data: dream,
    error,
    isLoading,
  } = useQuery({
    queryKey: ["dreams", "getDreamById", planId],
    queryFn: () => DreamService.getDream(numericPlanId),
  });

  useEffect(() => {
    if (error) {
      toast.error(`Nie udało się pobrać marzenia: ${error.message}`);
      redirect(routes.dreams.pathname); // redirect to the dreams page if there is an error
    }
  }, [error]);

  const completeDreamMutation = useMutation({
    mutationFn: async ({ dreamId }: { dreamId: number }) => {
      const res = DreamService.completeDream(dreamId);
      toast.promise(res, {
        loading: "Modyfikowanie marzenia...",
        success: "Marzenie ukończone!",
        error: (error) =>
          `Wystąpił błąd podczas modyfikowania marzenia: ${error.message}`,
      });
      return await res;
    },
    onSuccess: () => {
      // Invalidate queries to refetch data after mutation
      queryClient.invalidateQueries({ queryKey: ["dreams"] });
      queryClient.invalidateQueries({
        queryKey: ["dreams", "getDreamById", planId],
      });
    },
  });

  const addSubDreamMutation = useMutation({
    mutationFn: async (data: NewSubDreamReq) => {
      const res = DreamService.addSubDream(data);
      toast.promise(res, {
        loading: "Dodawanie składowej marzenia...",
        success: "Składowa marzenia dodana!",
        error: (error) =>
          `Wystąpił błąd podczas dodawania składowej marzenia marzenia: ${error.message}`,
      });
      return await res;
    },
    onSuccess: () => {
      // Invalidate queries to refetch data after mutation
      queryClient.invalidateQueries({ queryKey: ["dreams"] });
      queryClient.invalidateQueries({
        queryKey: ["dreams", "getDreamById", planId],
      });
    },
  });

  const deleteDreamMutation = useMutation({
    mutationFn: async () => {
      const res = DreamService.deleteDream(dream.planId);
      toast.promise(res, {
        loading: "Usuwanie marzenia...",
        success: "Marzenie usunięte!",
        error: (error) =>
          `Wystąpił błąd podczas usuwania marzenia: ${error.message}`,
      });
      return await res;
    },
    onSuccess: () => {
      // Invalidate queries to refetch data after mutation
      queryClient.invalidateQueries({ queryKey: ["dreams"] });
      router.replace(routes.dreams.pathname); // redirect to the dreams page after deletion
    },
  });

  const completeSubDreamMutation = useMutation({
    mutationFn: async (subDreamId: number) => {
      const res = DreamService.completeSubDream(subDreamId);
      toast.promise(res, {
        loading: "Zaznaczanie składowej marzenia jako ukończona...",
        success: "Składowa marzenia oznaczona jako ukończona!",
        error: (error) =>
          `Wystąpił błąd podczas zaznaczania składowej marzenia jako ukończona: ${error.message}`,
      });
      return await res;
    },
    onSuccess: () => {
      // Invalidate queries to refetch data after mutation
      queryClient.invalidateQueries({
        queryKey: ["dreams", "getDreamById", planId],
      });
    },
  });

  const deleteSubDreamMutation = useMutation({
    mutationFn: async (subDreamId: number) => {
      const res = DreamService.deleteSubDream(subDreamId);
      toast.promise(res, {
        loading: "Usuwanie składowej marzenia...",
        success: "Składowa marzenia usunięta!",
        error: (error) =>
          `Wystąpił błąd podczas usuwania składowej marzenia: ${error.message}`,
      });
      return await res;
    },
    onSuccess: () => {
      // Invalidate queries to refetch data after mutation
      queryClient.invalidateQueries({
        queryKey: ["dreams", "getDreamById", planId],
      });
    },
  });

  if (isLoading || !dream) {
    return <LoadingSpinner />; // Show a loading spinner while fetching data
  }

  return (
    <>
      {showPopup && (
        <AddSubDreamComponentPopup
          onSubmit={(data) => {
            addSubDreamMutation.mutate({
              planId: dream.planId,
              name: data.componentName,
              description: data.description,
              amount: data.amount,
              currencyId: data.currency.currencyId,
            });
            setShowPopup(false);
          }}
          onClose={() => {
            setShowPopup(false);
          }}
          currency={dream.currency}
        />
      )}
      <div className="flex flex-col h-full">
        <header>
          <div className="flex flex-row items-center justify-between mb-5">
            <h2 className="text-3xl md:text-4xl lg:text-5xl font-bold">
              {dream.name}
            </h2>
            <p className="font-lato text-xl mt-3 mb-2">
              <span>
                {Math.min(dream.actualAmount, dream.amount).toLocaleString(
                  undefined,
                  {
                    minimumFractionDigits: 0,
                    maximumFractionDigits: 2,
                  }
                )}{" "}
                {dream.currency.isoCode}
              </span>
              <span className="mx-1">/</span>
              <span>
                {dream.amount} {dream.currency.isoCode}
              </span>
            </p>
          </div>
          <ProgressBar progress={dream.actualAmount / dream.amount} />
          {dream.canBeCompleted && (
            <p className="text-sm mt-6">Można wykonać!</p>
          )}
          {dream.completed && <p className="text-sm mt-1">Zrealizowano!</p>}
          <p className="mt-5">Opis: {dream.description}</p>
        </header>
        <article className="flex-grow mt-5 w-full h-[70vh]  overflow-hidden">
          <div className="overflow-y-auto h-full">
            <h3 className="text-2xl font-bold mb-5">Składowe marzenia:</h3>
            {dream.subplans.length === 0 && (
              <p className="text-sm text-gray-500">
                Marzenie nie ma składowych
              </p>
            )}
            <div className="flex flex-col gap-5 ">
              {dream.subplans.map((subDream) => (
                <SubDreamCard
                  key={subDream.subplanId}
                  subdream={subDream}
                  onCompleteClicked={() => {
                    completeSubDreamMutation.mutate(subDream.subplanId);
                  }}
                  onDeleteClicked={() => {
                    deleteSubDreamMutation.mutate(subDream.subplanId);
                  }}
                />
              ))}
            </div>
          </div>
        </article>
        <footer className="grid grid-cols-1 md:grid-cols-3 gap-x-10 gap-y-5 mt-10">
          <DarkButton
            icon={"add"}
            text={"Dodaj składową marzenia"}
            onClick={() => {
              setShowPopup(true);
            }}
            className="bg-green-500 hover:bg-green-600 text-white"
          />
          <DarkButton
            icon={"check_circle_outline"}
            text={"Zrealizuj marzenie"}
            onClick={() => {
              completeDreamMutation.mutate({
                dreamId: numericPlanId,
              });
            }}
            className="bg-blue-500 hover:bg-blue-600 text-white"
          />
          <DarkButton
            icon={"delete_outline"}
            text={"Usuń marzenie"}
            onClick={() => {
              deleteDreamMutation.mutate();
            }}
            className="bg-red-500 hover:bg-red-600 text-white"
          />
        </footer>
      </div>
    </>
  );
}
