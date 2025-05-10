import {CartesianGrid, Label, Line, LineChart, XAxis, YAxis} from "recharts";
import {
  Card,
  CardContent,
  CardFooter,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import {
  ChartContainer,
  ChartTooltip,
} from "@/components/ui/chart";
import * as React from "react";
import {useContext, useEffect, useState} from "react";
import { useDashboardService } from "@/services/DashboardService";
import DarkButton from "@/components/DarkButton";
import MainChartPopup from "@/components/dashboard/charts/MainChartPopup";
import LoadingSpinner from "@/components/general/LoadingSpinner";
import toast from "react-hot-toast";
import { useQuery } from "@tanstack/react-query";
import {useTransactionService} from "@/services/TransactionService";
import CustomChartTooltip from "@/components/dashboard/charts/CustomTooltip";
import {MainChartContext} from "@/components/dashboard/charts/MainChartContext";
import {MainChartConfig} from "@/components/dashboard/charts/chartsConfig";
import {useDreamsService} from "@/services/DreamsService";
import { ReferenceLine } from "recharts";
import {getRoundedDomain} from "@/lib/utils";

export function MainChart() {
  const [showMainChartPopup, setShowMainChartPopup] = useState<boolean>(false);
  const [padding, setPadding] = useState<{ left: number; right: number }>({left: 0, right: 0});
  const { startDate, endDate, showDreams, showSubDreams } = useContext(MainChartContext);
  const DashboardService = useDashboardService();
  const TransactionService = useTransactionService();
  const DreamsService = useDreamsService();

  const { data: chartData, isLoading, isError, error} = useQuery({
    queryKey: [
      "dashboard",
      "mainChartData",
      startDate.format("YYYY-MM-DD"),
      endDate.format("YYYY-MM-DD"),
    ],
    queryFn: () =>
      DashboardService.getMainChartData(
        startDate.format("YYYY-MM-DD"),
        endDate.format("YYYY-MM-DD")
      ),
  });

  useEffect(() => {
    const updatePadding = () => {
      const width = window.innerWidth;

      if (!showDreams && !showSubDreams) {
        setPadding({ left: 20, right: 40 });
      } else if (width <= 640) {
        setPadding({ left: 90, right: 40 });
      } else {
        setPadding({ left: 100, right: 50 });
      }
    };

    updatePadding();

    window.addEventListener("resize", updatePadding);
    return () => {
      window.removeEventListener("resize", updatePadding);
    };
  }, [showDreams, showSubDreams]);

  const { data: allTransactionsFromRange } = useQuery({
    queryKey: ["allTransactionsFromRange", startDate.format("YYYY-MM-DD"), endDate.format("YYYY-MM-DD")],
    queryFn: () =>
        TransactionService.getTransactionsFromRange(
            startDate.format("YYYY-MM-DD"),
            endDate.format("YYYY-MM-DD")
        ),
  });

  const { data: dreams } = useQuery({
    queryKey: ["dreamsWithSubplans"],
    queryFn: () => DreamsService.getChartDreamsData(),
  });

  if (isError) {
    toast.error("Failed to fetch main chart data: " + error.message);
  }

  if (isLoading || !chartData || !allTransactionsFromRange) {
    return <LoadingSpinner />;
  }

  const domain = showDreams && showSubDreams
      ? getRoundedDomain(dreams)
      : showSubDreams
          ? getRoundedDomain(dreams, 100, 'SUBPLAN')
          : showDreams
              ? getRoundedDomain(dreams, 100, 'PLAN')
              : ['auto', 'auto'];

  return (
    <>
      {showMainChartPopup && (
        <MainChartPopup
          onCloseAction={() => setShowMainChartPopup(false)}
        />
      )}
      <Card className="flex flex-col w-full h-full bg-transparent z-10 border-none">
        <CardHeader className="flex justify-between items-center">
          <div>
            <CardTitle className="text-xl">Wykres cashflow</CardTitle>
          </div>
          <div className="w-30">
            <DarkButton
              text={"Opcje wykresu"}
              onClick={() => setShowMainChartPopup(!showMainChartPopup)}
            />
          </div>
        </CardHeader>
        <CardContent className="w-full flex flex-col justify-center items-center overflow-hidden my-0 px-0">
          <ChartContainer
            config={MainChartConfig}
            className="w-full max-w-full h-full flex justify-center"
          >
            <LineChart
              accessibilityLayer
              data={chartData}
              className="w-full h-full"
            >
              <CartesianGrid vertical={false} />
              <XAxis
                dataKey="date"
                tickLine={false}
                axisLine={false}
                tickMargin={8}
                tickFormatter={(value) =>
                  new Date(value).toLocaleDateString("pl-PL", {
                    day: "2-digit",
                    month: "2-digit",
                    year: "2-digit",
                  })
                }
                className="font-lato"
                padding={padding}
              />
              <YAxis
                axisLine={false}
                tickLine={false}
                tickMargin={16}
                tickFormatter={(value) => (Math.round(value / 50) * 50).toString()}
                className="font-lato"
                domain={domain}
              />
              <ChartTooltip
                  cursor={false}
                  content={(props) =>
                      props.active ? (
                          <CustomChartTooltip {...props} transactions={allTransactionsFromRange.transactions} />
                      ) : null
                  }
              />
              <Line
                dataKey="amount"
                type="linear"
                stroke="#262626"
                strokeWidth={2}
                dot={true}
              />
              {showDreams && dreams?.filter(dream => dream.planType === 'PLAN')
                  .map((dream, i) => (
                  <ReferenceLine
                      key={`dream-line-${i}-${dream.name}`}
                      y={dream.requiredAmount}
                      stroke="#c82026"
                      strokeDasharray="3 3"
                  >
                    <Label
                        value={dream.name}
                        position="insideTopLeft"
                        fill="#c82026"
                        fontSize={12}
                    />
                  </ReferenceLine>
              ))}

              {showSubDreams && dreams?.filter(dream => dream.planType === 'SUBPLAN')
                  .map((dream, i) => (
                      <ReferenceLine
                          key={`subdream-line-${i}-${dream.name}`}
                          y={dream.requiredAmount}
                          stroke="#c82026"
                          strokeDasharray="2 4"
                      >
                        <Label
                            value={dream.name}
                            position="insideTopLeft"
                            fill="#c82026"
                            fontSize={12}
                        />
                      </ReferenceLine>
                  )
              )}
            </LineChart>
          </ChartContainer>
        </CardContent>
        <CardFooter className="w-full flex flex-wrap justify-center my-0 py-0">
          <div className="flex items-center gap-2">
            <span
              className="w-5 h-2"
              style={{ backgroundColor: "#262626" }}
            ></span>
            <span className="text-sm text-muted-foreground">Stan konta</span>
          </div>
        </CardFooter>
      </Card>
    </>
  );
}
