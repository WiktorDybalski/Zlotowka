"use client";

import * as React from "react";
import { Label, Pie, PieChart } from "recharts";
import {
  Card,
  CardContent,
  CardFooter,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { ChartContainer, ChartTooltip } from "@/components/ui/chart";
import { useDashboardService } from "@/services/DashboardService";
import formatMoney from "@/utils/formatMoney";
import LoadingSpinner from "@/components/general/LoadingSpinner";
import { PieChartConfig } from "@/components/dashboard/charts/chartsConfig";
import CustomLabel from "@/components/dashboard/charts/CustomLabel";
import { useQueryWithToast } from "@/lib/data-grabbers";

export function PieSideChart() {
  const DashboardService = useDashboardService();

  const { data, isLoading } = useQueryWithToast({
    queryKey: ["dashboard", "getPieSideChartData"],
    queryFn: DashboardService.getPieSideChartData,
  });

  if (isLoading || !data) {
    return <LoadingSpinner />;
  }

  const formattedChartData = [
    {
      category: PieChartConfig.Income.label,
      value: data.monthlyIncome,
      fill: PieChartConfig.Income.color,
    },
    {
      category: PieChartConfig.Expenses.label,
      value: data.monthlyExpenses,
      fill: PieChartConfig.Expenses.color,
    },
  ];
  const total = data.monthlyBalance;

  return (
    <Card className="flex flex-col w-full h-full bg-transparent z-10 border-none">
      <CardHeader className="items-center pb-0">
        <CardTitle className="text-xl">Podsumowanie miesiąca</CardTitle>
      </CardHeader>

      <CardContent className="flex-1 flex items-center justify-center p-0">
        <ChartContainer
          config={PieChartConfig}
          className="w-full h-full max-w-md mx-auto"
        >
          <PieChart>
            <ChartTooltip
              cursor={false}
              content={({ active, payload }) => {
                if (active && payload && payload.length) {
                  return (
                    <div className="font-lato p-2 bg-background dark:bg-dark dark:text-background z-[999] border rounded shadow">
                      <p>{formatMoney(Number(payload[0].value))} PLN</p>
                    </div>
                  );
                }
                return null;
              }}
            />
            {formattedChartData.some((item) => item.value !== 0) ? (
              <Pie
                data={formattedChartData}
                dataKey="value"
                nameKey="category"
                innerRadius="70%"
                outerRadius="85%"
                strokeWidth={3}
                stroke="#fa"
                paddingAngle={4}
              >
                <Label
                  content={(props) => <CustomLabel {...props} total={total} />}
                />
              </Pie>
            ) : (
              <text
                x="50%"
                y="50%"
                textAnchor="middle"
                dominantBaseline="middle"
                className="text-lg"
              >
                Nie ma przychodów i wydatków
              </text>
            )}
          </PieChart>
        </ChartContainer>
      </CardContent>
      <CardFooter className="w-full flex flex-wrap justify-center gap-x-8 gap-y-2 px-4">
        {formattedChartData.map((item) => (
          <div key={item.category} className="flex items-center gap-2">
            <span
              className="w-5 h-2"
              style={{ backgroundColor: item.fill }}
            ></span>
            <span className="text-sm text-muted-foreground font-lato">
              {item.category} ({item.value} PLN)
            </span>
          </div>
        ))}
      </CardFooter>
    </Card>
  );
}
