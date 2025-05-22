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
  onEditClicked?: () => void;
}

export default function SubDreamCard({
  subdream,
  onCompleteClicked = () => {},
  onDeleteClicked = () => {},
  onEditClicked = () => {},
}: SubDreamCardProps) {
  return (
    <div>
      <GenericCard className={`${subdream.completed ? "bg-lightAccent" : ""}`}>
        <ThreeElementsCard
          top={
            <>
              <div className="flex flex-row items-center justify-between">
                <div className="flex items-center gap-x-2">
                  <h3 className="text-2xl font-bold">{subdream.name} </h3>
                  {subdream.completed && (
                    <span
                      className="material-symbols "
                      style={{ fontSize: "2.5rem" }}
                    >
                      done
                    </span>
                  )}
                </div>

                <p className="font-lato text-xl mt-3 mb-2">
                  <span>
                    {Math.min(
                      subdream.actualAmount,
                      subdream.amount
                    ).toLocaleString(undefined, {
                      minimumFractionDigits: 0,
                      maximumFractionDigits: 2,
                    })}
                  </span>
                  <span className="mx-1">/</span>
                  <span>
                    {subdream.amount} {subdream.currency.isoCode}
                  </span>
                </p>
              </div>
              <ProgressBar progress={subdream.actualAmount / subdream.amount} />

              <div className="flex flex-col gap-2 mt-3">
                {subdream.canBeCompleted && !subdream.completed && (
                  <p className="text-sm">Można zrealizować!</p>
                )}
                {subdream.completed && (
                  <p className="text-sm ">Zrealizowano!</p>
                )}
                <p className="text-base">
                  <span className="font-extrabold">Opis:</span>{" "}
                  {subdream.description
                    ? subdream.description
                    : "Nie podano opisu"}
                </p>
              </div>
            </>
          }
          bottom={
            <>
              <footer className="mt-6 flex justify-end gap-4">
                <DarkButton
                  icon={"check_circle_outline"}
                  onClick={onCompleteClicked}
                  className="bg-green-600 hover:bg-green-700 text-white max-w-14"
                  disabled={!(subdream.canBeCompleted && !subdream.completed)}
                />
                <DarkButton
                  icon={"edit"}
                  onClick={onEditClicked}
                  className="bg-lightAccentDark hover:bg-backgroundLightDark text-white max-w-14"
                />
                <DarkButton
                  icon={"delete_outline"}
                  onClick={onDeleteClicked}
                  className="bg-red-500 hover:bg-red-600 text-white max-w-14"
                />
              </footer>
            </>
          }
        />
      </GenericCard>
    </div>
  );
}
