"use client"

import * as React from "react"
import { Label, Pie, PieChart, ResponsiveContainer } from "recharts"
import { Card, CardContent, CardFooter, CardHeader, CardTitle } from "@/components/ui/card"
import { type ChartConfig, ChartContainer, ChartTooltip, ChartTooltipContent } from "@/components/ui/chart"

const chartData = [
  { category: "Income", visitors: 275, fill: "#262626" },
  { category: "Expenses", visitors: 200, fill: "#e9e9e9" },
];

const chartConfig = {
  visitors: {
    label: "Visitors",
  },
  Income: {
    label: "Income",
    color: "#262626",
  },
  Expenses: {
    label: "Expenses",
    color: "#e9e9e9",
  },
} satisfies ChartConfig;

export function BarChart() {
  return (
      <Card className="flex flex-col w-full h-full bg-transparent z-10">
        <CardHeader className="items-center pb-0">
          <CardTitle className="text-xl">Podsumowanie miesiąca</CardTitle>
        </CardHeader>

        <CardContent className="flex-1 flex items-center justify-center p-0">
          <ChartContainer config={chartConfig} className="w-full h-full max-w-md mx-auto">
            <ResponsiveContainer width="100%" height={300}>
              <PieChart>
                <ChartTooltip cursor={false} content={<ChartTooltipContent hideLabel />} />
                <Pie
                    data={chartData}
                    dataKey="visitors"
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
                                <tspan x={viewBox.cx} y={viewBox.cy} className="text-xl sm:text-2xl md:text-3xl font-lato">
                                  6 500 PLN
                                </tspan>
                                <tspan x={viewBox.cx} y={(viewBox.cy || 0) + 24} className="text-sm">
                                  Total
                                </tspan>
                              </text>
                          )
                        }
                        return null;
                      }}
                  />
                </Pie>
              </PieChart>
            </ResponsiveContainer>
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
