import { CartesianGrid, Line, LineChart, XAxis, YAxis } from "recharts"
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from "@/components/ui/card"
import { type ChartConfig, ChartContainer, ChartTooltip, ChartTooltipContent } from "@/components/ui/chart"
import * as React from "react";

const chartData = [
  { month: "January", desktop: 186 },
  { month: "February", desktop: 305 },
  { month: "March", desktop: 237 },
  { month: "April", desktop: 73 },
  { month: "May", desktop: 209 },
  { month: "June", desktop: 214 },
]

const chartConfig = {
  desktop: {
    label: "Desktop",
    color: "#262626",
  },
} satisfies ChartConfig

export function MainChart() {
  return (
      <Card className="flex flex-col w-full h-full bg-transparent z-10">
        <CardHeader className="flex justify-between items-center">
          <div>
            <CardTitle className="text-xl">Wykres cashflow</CardTitle>
            <CardDescription>January - June 2024</CardDescription>
          </div>
          <div>
            <p>Kiedyś pewnie tu będzie datepicker</p>
          </div>
        </CardHeader>
        <CardContent className="w-full flex flex-col justify-center items-center overflow-hidden my-0 px-0">
          <ChartContainer config={chartConfig} className="w-full max-w-full h-full flex justify-center">
            <LineChart
                accessibilityLayer
                data={chartData}
                className="w-full h-full"
            >
              <CartesianGrid vertical={false} />
              <XAxis
                  dataKey="month"
                  tickLine={false}
                  axisLine={false}
                  tickMargin={8}
                  tickFormatter={(value) => value.slice(0, 3)}
                  padding={{ left: 100, right: 100 }}
              />
              <YAxis
                  axisLine={false}
                  tickLine={false}
                  tickMargin={8}
              />
              <ChartTooltip cursor={false} content={<ChartTooltipContent hideLabel />} />
              <Line dataKey="desktop" type="natural" stroke="#262626" strokeWidth={2} dot={false} />
            </LineChart>
          </ChartContainer>
        </CardContent>
        <CardFooter className="w-full flex flex-wrap justify-center my-0 py-0">
          <div className="flex items-center gap-2">
            <span className="w-5 h-2" style={{ backgroundColor: "#262626" }}></span>
            <span className="text-sm text-muted-foreground">Current state</span>
          </div>
        </CardFooter>
      </Card>
  )
}
