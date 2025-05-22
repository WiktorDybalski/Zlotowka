"use client";

import GenericCard from "@/components/dashboard/cards/generic/GenericCard";
import { PieSideChart } from "@/components/dashboard/charts/PieSideChart";
import AddTransaction from "@/components/transactions/AddTransaction";
import { useState } from "react";
import DarkButton from "@/components/DarkButton";
import { MainChart } from "@/components/dashboard/charts/MainChart";
import PinnedDreamCard from "@/components/dashboard/cards/PinnedDreamCard";
import MonthForecastCard from "@/components/dashboard/cards/MonthForecastCard";
import CardsPopup from "@/components/dashboard/components/CardsPopup";
import CurrentBalanceCard from "@/components/dashboard/cards/CurrentBalanceCard";
import {
  CardComponents,
  CardId,
} from "@/interfaces/dashboard/cards/CardComponents";
import NextExpenseCard from "@/components/dashboard/cards/NextExpenseCard";
import NextIncomeCard from "@/components/dashboard/cards/NextIncomeCard";
import { DreamProvider } from "@/components/dreams/DreamsContext";
import { MainChartProvider } from "@/components/providers/MainChartContext";
import DashboardTransactionTable from "@/components/dashboard/DashboardTransactionTable";

export default function Dashboard() {
  const [showAddTransaction, setShowAddTransaction] = useState(false);
  const [showPopup, setShowPopup] = useState(false);
  const [selectedCards, setSelectedCards] = useState<CardId[]>([
    "nextExpense",
    "currentBalance",
    "monthForecast",
  ]);

  const cardComponents: CardComponents = {
    pinnedDream: <PinnedDreamCard />,
    monthForecast: <MonthForecastCard />,
    currentBalance: <CurrentBalanceCard />,
    nextIncome: <NextIncomeCard />,
    nextExpense: <NextExpenseCard />,
  };

  return (
    <>
      <DreamProvider>
        {showAddTransaction && (
          <AddTransaction setShowAddTransaction={setShowAddTransaction} />
        )}
        {showPopup && (
          <CardsPopup
            onClose={() => setShowPopup(false)}
            selectedCards={selectedCards}
            setSelectedCards={setSelectedCards}
          />
        )}

        <div className="relative w-full min-h-screen grid px-6 py-12 grid-cols-1 lg:px-16 lg:py-8 lg:grid-rows-[200px_600px_450px_auto] lg:grid-cols-3 gap-6 2xl:px-20">
          <div className="absolute top-3 right-5 lg:top-8 2xl:right-5 hover:cursor-pointer">
            <span
              className="material-symbols"
              onClick={() => setShowPopup(!showPopup)}
            >
              dashboard_customize
            </span>
          </div>

          {selectedCards.map((cardId) => {
            return (
              <GenericCard key={cardId}>{cardComponents[cardId]}</GenericCard>
            );
          })}

          <MainChartProvider>
            <GenericCard className="lg:col-span-3">
              <MainChart />
            </GenericCard>

            <GenericCard className="lg:col-span-2">
              <DashboardTransactionTable />
              <div></div>
            </GenericCard>
          </MainChartProvider>

          <GenericCard>
            <PieSideChart />
          </GenericCard>

          <div className="w-full sm:w-52 h-10">
            <DarkButton
              icon={"add"}
              text={"Dodaj transakcje"}
              onClick={() => {
                setShowAddTransaction(!showAddTransaction);
              }}
            />
          </div>
        </div>
      </DreamProvider>
    </>
  );
}
