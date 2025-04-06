import { CartesianGrid, Line, LineChart, XAxis, YAxis } from "recharts"
import { Card, CardContent, CardFooter, CardHeader, CardTitle } from "@/components/ui/card"
import { type ChartConfig, ChartContainer, ChartTooltip, ChartTooltipContent } from "@/components/ui/chart"
import * as React from "react";
import {useEffect, useState} from "react";
import {MainChartData} from "@/mocks/MainChartData";

const chartConfig = {
  value: {
    label: "Stan konta",
    color: "#262626",
  },
} satisfies ChartConfig

export function MainChart() {
  const [padding, setPadding] = useState<{ left: number; right: number }>({ left: 30, right: 30 });

  useEffect(() => {
    const updatePadding = () => {
      const width = window.innerWidth;

      if (width <= 640) {
        setPadding({ left: 0, right: 40 });
      } else if (width <= 1500) {
        setPadding({ left: 20, right: 40 });
      } else {
        setPadding({ left: 70, right: 70 });
      }
    };

    updatePadding();
    window.addEventListener("resize", updatePadding);

    return () => window.removeEventListener("resize", updatePadding);
  }, []);

  return (
      <Card className="flex flex-col w-full h-full bg-transparent z-10 border-none">
        <CardHeader className="flex justify-between items-center">
          <div>
            <CardTitle className="text-xl">Wykres cashflow</CardTitle>
          </div>
          <div>
            <h3>Datepicker nie dziala</h3>
          </div>
        </CardHeader>
        <CardContent className="w-full flex flex-col justify-center items-center overflow-hidden my-0 px-0">
          <ChartContainer config={chartConfig} className="w-full max-w-full h-full flex justify-center">
            <LineChart
                accessibilityLayer
                data={MainChartData}
                className="w-full h-full"
            >
              <CartesianGrid vertical={false} />
              <XAxis
                  dataKey="date"
                  tickLine={false}
                  axisLine={false}
                  tickMargin={8}
                  tickFormatter={(value) =>
                      new Date(value)
                          .toLocaleDateString('pl-PL', { day: '2-digit', month: '2-digit', year: '2-digit' })
                  }
                  padding={padding}
                  className="font-(family-name:--font-lato)"
              />
              <YAxis
                  axisLine={false}
                  tickLine={false}
                  tickMargin={16}
              />
              <ChartTooltip cursor={false} content={<ChartTooltipContent hideLabel />} />
              <Line dataKey="value" type="natural" stroke="#262626" strokeWidth={2} dot={false} />
            </LineChart>
          </ChartContainer>
        </CardContent>
        <CardFooter className="w-full flex flex-wrap justify-center my-0 py-0">
          <div className="flex items-center gap-2">
            <span className="w-5 h-2" style={{ backgroundColor: "#262626" }}></span>
            <span className="text-sm text-muted-foreground">Stan konta</span>
          </div>
        </CardFooter>
      </Card>
  )
}
