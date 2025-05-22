"use client";

import DarkButton from "@/components/DarkButton";
import AddSubDreamComponentPopup from "@/components/dreams/AddSubDreamPopUp";
import SubDreamCard from "@/components/dreams/SubDreamCard";
import LoadingSpinner from "@/components/general/LoadingSpinner";
import { ProgressBar } from "@/components/general/ProgressBar";
import routes from "@/routes";
import {
  NewSubDreamReq,
  SubDream,
  useDreamsService,
} from "@/services/DreamsService";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { redirect, useParams, useRouter } from "next/navigation";
import { useEffect, useState } from "react";
import toast from "react-hot-toast";
import { useDreamContext } from "@/components/dreams/DreamsContext";
import { useQueryWithToast } from "@/lib/data-grabbers";
import AddDreamComponentPopup from "@/components/dreams/AddDreamPopUp";

export default function DreamDetailsPage() {
  const { planId } = useParams();
  const { pickedDream, handlePickDream } = useDreamContext();
  const DreamService = useDreamsService();
  const numericPlanId = Number(planId);

  const [showAddSubDreamPopup, setShowAddSubDreamPopup] = useState(false);

  const [showEditSubDreamPopup, setShowEditSubDreamPopup] = useState(false);
  const [subDreamForEdit, setSubDreamForEdit] = useState<SubDream | null>(null);

  const [showEditDreamPopup, setShowEditDreamPopup] = useState(false);

  const queryClient = useQueryClient();
  const router = useRouter();

  useEffect(() => {
    if (isNaN(numericPlanId)) {
      redirect(routes.dreams.pathname);
    }
  }, [numericPlanId]);

  const { data: dream, isLoading } = useQueryWithToast({
    queryKey: ["dreams", "getDreamById", planId],
    queryFn: () => DreamService.getDream(numericPlanId),
  });

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
          `Wystąpił błąd podczas dodawania składowej marzenia: ${error.message}`,
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

  const modifyDreamMutation = useMutation({
    mutationFn: async (dream: NewSubDreamReq) => {
      const res = DreamService.modifyDream(dream, numericPlanId);
      toast.promise(res, {
        loading: "Modyfikowanie marzenia...",
        success: "Marzenie zmodyfikowane!",
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

  const modifySubDreamMutation = useMutation({
    mutationFn: async ({
      subDream,
      subDreamId,
    }: {
      subDream: NewSubDreamReq;
      subDreamId: number;
    }) => {
      const res = DreamService.modifySubDream(subDream, subDreamId);
      toast.promise(res, {
        loading: "Modyfikowanie składowej marzenia...",
        success: "Składowa marzenia zmodyfikowana!",
        error: (error) =>
          `Wystąpił błąd podczas modyfikowania składowej marzenia: ${error.message}`,
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
    return (
      <div className="absolute top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2">
        <LoadingSpinner />
      </div>
    );
  }

  return (
    <>
      {showAddSubDreamPopup && (
        <AddSubDreamComponentPopup //add subream
          onSubmit={(data) => {
            addSubDreamMutation.mutate({
              planId: dream.planId,
              name: data.componentName,
              description: data.description,
              amount: data.amount,
              currencyId: data.currency.currencyId,
            });
            setShowAddSubDreamPopup(false);
          }}
          onClose={() => {
            setShowAddSubDreamPopup(false);
          }}
          currency={dream.currency}
        />
      )}
      {dream && showEditDreamPopup && (
        <AddDreamComponentPopup //edit main dream
          onClose={() => setShowEditDreamPopup(false)}
          onSubmit={(data) => {
            modifyDreamMutation.mutate({
              name: data.componentName,
              description: data.description,
              amount: data.amount,
              currencyId: data.currency.currencyId,
              planId: dream.planId,
            });
            setShowEditDreamPopup(false);
          }}
          providedDream={{
            componentName: dream.name,
            description: dream.description,
            amount: dream.amount,
            currency: dream.currency,
          }}
          windowTitle={"Edytuj marzenie"}
          submitButtonText={"Zapisz"}
        />
      )}
      {dream && showEditSubDreamPopup && subDreamForEdit && (
        <AddSubDreamComponentPopup //edit subdream
          onClose={() => setShowEditSubDreamPopup(false)}
          onSubmit={(data) => {
            modifySubDreamMutation.mutate({
              subDream: {
                name: data.componentName,
                description: data.description,
                amount: data.amount,
                currencyId: data.currency.currencyId,
                planId: dream.planId,
              },
              subDreamId: subDreamForEdit.subplanId,
            });
          }}
          currency={dream.currency}
          windowTitle="Edytuj składową marzenia"
          submitButtonText="Zapisz"
          providedSubDream={{
            componentName: subDreamForEdit.name,
            description: subDreamForEdit.description,
            amount: subDreamForEdit.amount,
            currency: subDreamForEdit.currency,
          }}
        />
      )}
      <div className="flex flex-col h-full overflow-x-hidden">
        <header>
          <div className="flex flex-row items-center justify-between mb-5">
            <div className="flex items-center gap-x-2">
              <h2 className="text-3xl md:text-4xl lg:text-5xl font-semibold">
                {`# ${dream.name}`}
              </h2>
              <span
                className={`${
                  dream.planId === pickedDream
                    ? "material-symbol-outlined"
                    : "material-symbols"
                }`}
                onClick={() => handlePickDream(dream.planId)}
                style={{ fontSize: "2.5rem" }}
              >
                keep
              </span>
              {dream.completed && (
                <span
                  className="material-symbols "
                  style={{ fontSize: "4rem" }}
                >
                  done
                </span>
              )}
            </div>
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
          <div className="flex flex-col gap-2 mt-3">
            {dream.canBeCompleted && !dream.completed && (
              <p className="text-sm">Można zrealizować!</p>
            )}
            {dream.completed && (
              <p className="text-sm">Marzenie zrealizowane!</p>
            )}
            <p>
              <span className="font-extrabold">Opis:</span>{" "}
              {dream.description ? dream.description : "Nie podano opisu"}
            </p>
          </div>
        </header>

        <footer className="grid grid-cols-1 md:grid-cols-4 sm:grid-cols-2 gap-x-10 gap-y-5 mt-10">
          <DarkButton
            icon={"check_circle_outline"}
            text={"Zrealizuj"}
            onClick={() => {
              completeDreamMutation.mutate({
                dreamId: numericPlanId,
              });
            }}
            className="bg-green-600 hover:bg-green-700 text-white"
            disabled={!(dream.canBeCompleted && !dream.completed)}
          />
          <DarkButton
            icon={"add"}
            text={"Dodaj składową"}
            onClick={() => {
              setShowAddSubDreamPopup(true);
            }}
            className="bg-lightAccentDark hover:bg-backgroundLightDark text-white"
          />
          <DarkButton
            icon={"edit"}
            text={"Edytuj"}
            onClick={() => {
              setShowEditDreamPopup(true);
            }}
            className="bg-lightAccentDark hover:bg-backgroundLightDark text-white"
          />
          <DarkButton
            icon={"delete_outline"}
            text={"Usuń"}
            onClick={() => {
              deleteDreamMutation.mutate();
            }}
            className="bg-red-500 hover:bg-red-600 text-white"
          />
        </footer>

        <article className="flex-grow mt-12 w-full">
          <h3 className="text-3xl md:text-4xl font-bold">Składowe marzenia:</h3>
          <hr className="mb-5 mt-2 border-gray-300" />
          {dream.subplans.length === 0 && (
            <p className="text-sm text-gray-500">Marzenie nie ma składowych</p>
          )}
          <div className="grid grid-cols-1 md:grid-cols-2  gap-5">
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
                onEditClicked={() => {
                  setSubDreamForEdit(subDream);
                  setShowEditSubDreamPopup(true);
                }}
              />
            ))}
          </div>
        </article>
      </div>
    </>
  );
}
