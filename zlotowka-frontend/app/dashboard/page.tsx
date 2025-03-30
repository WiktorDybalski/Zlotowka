"use client"


import GenericCard from "@/components/cards/GenericCard";
import ThreeElementsCard from "@/components/cards/ThreeElementsCard";
import {BarChart} from "@/app/dashboard/BarChart";
import AddTransaction from "@/components/transactions/AddTransaction";
import {useState} from "react";
import DarkButton from "@/components/DarkButton";
import {MainChart} from "@/app/dashboard/MainChart";

export default function Dashboard() {
  const [showAddTransactions, setShowAddTransactions] = useState(false);

  return (
      <>
        {showAddTransactions && <AddTransaction setShowAddTransaction={setShowAddTransactions} />}

        <div className="w-full min-h-screen grid px-8 py-12 grid-cols-1 lg:p-6 lg:grid-rows-[200px_600px_450px_auto] lg:grid-cols-3 gap-6 xl:px-4 2xl:px-20">
          <ThreeElementsCard
              top={<p>Następny wydatek</p>}
              middle={<p>29 500.50zł</p>}
              bottom={<div className="flex justify-between">
                <p>Następny wydatek xyz</p>
                <p className="font-lato">28.03.2025</p>
              </div>}
          />

          <ThreeElementsCard
              top={<p>Prognoza finansowa</p>}
              middle={<div className="flex items-baseline font-lato">
                <p>59 000.50zł</p>
                <p className="text-base font-medium ml-3 text-red-700">(-40%)</p>
              </div> }
              bottom={<p>Szacowane saldo na koniec miesiąca</p>}
          />

          <ThreeElementsCard
              top={<p>Twój wybrany cel</p>}
              middle={<div className="flex flex-col">
                <p className="text-2xl">Wakacje na malediwach</p>
                <div className="w-7/8 h-5 my-2 bg-neutral-200 rounded-xl"></div>
              </div> }
              bottom={<p>Cel: <span className="font-lato">3 000zł</span></p>}
          />

          <GenericCard className="lg:col-span-3">
            <div className="w-full h-full">
              <MainChart />
            </div>
          </GenericCard>

          <GenericCard className="lg:col-span-2">Transactions</GenericCard>

          <GenericCard>
            <div className="w-full h-full">
              <BarChart />
            </div>
          </GenericCard>

          <div className="w-52 h-10">
            <DarkButton icon={"add"} text={"Dodaj transakcje"} onClick={() => setShowAddTransactions(!showAddTransactions)}/>
          </div>

        </div>
      </>

  );
}