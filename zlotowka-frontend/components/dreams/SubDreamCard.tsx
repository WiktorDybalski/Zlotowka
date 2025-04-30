"use client";

import React from "react";
import GenericCard from "../dashboard/cards/generic/GenericCard";
import { SubDream } from "@/services/DreamsService";
import ThreeElementsCard from "../dashboard/cards/generic/ThreeElementsCard";
import { ProgressBar } from "../general/ProgressBar";
import DarkButton from "../DarkButton";

interface SubDreamCardProps {
  subdream: SubDream;
  onCompleteClicked?: () => void;
  onDeleteClicked?: () => void;
}

export default function SubDreamCard({
  subdream,
  onCompleteClicked = () => {},
  onDeleteClicked = () => {},
}: SubDreamCardProps) {
  return (
    <div>
      <GenericCard
        className={`p-4 ${subdream.completed ? "bg-lightAccent" : ""}`}
      >
        <ThreeElementsCard
          top={
            <>
              <div className="flex flex-row items-center justify-between">
                <h3 className="text-2xl font-bold">{subdream.name}</h3>
                <p className="font-lato text-xl mt-3 mb-2">
                  <span>
                    {Math.min(
                      subdream.actualAmount,
                      subdream.amount
                    ).toLocaleString(undefined, {
                      minimumFractionDigits: 0,
                      maximumFractionDigits: 2,
                    })}{" "}
                    {subdream.currency.isoCode}
                  </span>
                  <span className="mx-1">/</span>
                  <span>
                    {subdream.amount} {subdream.currency.isoCode}
                  </span>
                </p>
              </div>
              <ProgressBar progress={subdream.actualAmount / subdream.amount} />
              <p className="text-sm mt-6">
                <span className="font-bold">Opis: </span>
                {subdream.description}
              </p>
            </>
          }
          middle={<></>}
          bottom={
            <>
              <footer className="flex flex-wrap items-center justify-end gap-x-10 gap-y-4">
                {subdream.canBeCompleted && !subdream.completed && (
                  <DarkButton
                    icon={"check_circle_outline"}
                    text={"Zrealizuj składową marzenia"}
                    onClick={onCompleteClicked}
                    className="bg-blue-500 hover:bg-blue-600 text-white max-w-[300px]"
                  />
                )}

                <DarkButton
                  icon={"delete_outline"}
                  text={"Usuń składową marzenia"}
                  onClick={onDeleteClicked}
                  className="bg-red-500 hover:bg-red-600 text-white max-w-[300px]"
                />
              </footer>
            </>
          }
        />
      </GenericCard>
    </div>
  );
}
