"use client"

import * as React from "react"
import { Label, Pie, PieChart } from "recharts"
import { Card, CardContent, CardFooter, CardHeader, CardTitle } from "@/components/ui/card"
import { type ChartConfig, ChartContainer, ChartTooltip } from "@/components/ui/chart"
import {useEffect, useState} from "react";
import DashboardService from "@/services/DashboardService";
import formatMoney from "@/utils/formatMoney";

const chartConfig = {
  value: {
    label: "Value",
  },
  Income: {
    label: "Przychody",
    color: "#262626",
  },
  Expenses: {
    label: "Wydatki",
    color: "#e9e9e9",
  },
} satisfies ChartConfig;

export function PieSideChart() {
  const [chartData, setChartData] = useState([]);
  const [total, setTotal] = useState("0.00");

  useEffect(() => {
    DashboardService.getPieSideChartData(1)
        .then((data) => {
          const formattedChartData = [
            {
              category: chartConfig.Income.label,
              value: data.income,
              fill: chartConfig.Income.color,
            },
            {
              category: chartConfig.Expenses.label,
              value: data.expenses,
              fill: chartConfig.Expenses.color,
            },
          ];
          setChartData(formattedChartData);
          setTotal(data.total)
        })
  }, [])

  return (
      <Card className="flex flex-col w-full h-full bg-transparent z-10 border-none">
        <CardHeader className="items-center pb-0">
          <CardTitle className="text-xl">Podsumowanie miesiąca</CardTitle>
        </CardHeader>

        <CardContent className="flex-1 flex items-center justify-center p-0">
          <ChartContainer config={chartConfig} className="w-full h-full max-w-md mx-auto">
              <PieChart>
                <ChartTooltip
                    cursor={false}
                    content={({ active, payload }) => {
                      if (active && payload && payload.length) {
                        return (
                            <div className="font-(family-name:--font-lato) p-2 bg-neutral-100 z-[999] border rounded shadow">
                              <p>{formatMoney(Number(payload[0].value))} PLN</p>
                            </div>
                        );
                      }
                      return null;
                    }}
                />
                {chartData.some(item => item.value != 0) ?
                    <Pie
                        data={chartData}
                        dataKey="value"
                        nameKey="category"
                        innerRadius="70%"
                        outerRadius="85%"
                        strokeWidth={3}
                        stroke="#fa"
                        paddingAngle={4}
                    >
                      <Label
                          content={({ viewBox }) => {
                            if (viewBox && "cx" in viewBox && "cy" in viewBox) {
                              return (
                                  <text x={viewBox.cx} y={viewBox.cy} textAnchor="middle" dominantBaseline="middle">
                                    <tspan x={viewBox.cx} y={viewBox.cy} className="text-lg sm:text-xl md:text-2xl font-(family-name:--font-lato)">
                                      {formatMoney(Number(total))} PLN
                                    </tspan>
                                    <tspan x={viewBox.cx} y={(viewBox.cy || 0) + 24} className="text-sm">
                                      Suma
                                    </tspan>
                                  </text>
                              )
                            }
                            return null;
                          }}
                      />
                    </Pie> :
                    <text x="50%" y="50%" textAnchor="middle" dominantBaseline="middle" className="text-lg">
                      Nie ma przychodów i wydatków
                    </text>
                }
              </PieChart>
          </ChartContainer>
        </CardContent>
        <CardFooter className="w-full flex flex-wrap justify-center gap-x-8 gap-y-2 px-4">
          {chartData.map((item) => (
              <div key={item.category} className="flex items-center gap-2">
                <span className="w-5 h-2" style={{ backgroundColor: item.fill }}></span>
                <span className="text-sm text-muted-foreground">{item.category}</span>
              </div>
          ))}
        </CardFooter>
      </Card>
  )
}
