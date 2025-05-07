import { CartesianGrid, Line, LineChart, XAxis, YAxis } from "recharts";
import {
  Card,
  CardContent,
  CardFooter,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import {
  type ChartConfig,
  ChartContainer,
  ChartTooltip,
  ChartTooltipContent,
} from "@/components/ui/chart";
import * as React from "react";
import { useEffect, useState } from "react";
import { useDashboardService } from "@/services/DashboardService";
import DarkButton from "@/components/DarkButton";
import RangePickerPopup from "@/components/dashboard/charts/RangePickerPopup";
import LoadingSpinner from "@/components/general/LoadingSpinner";
import toast from "react-hot-toast";
import dayjs, { Dayjs } from "dayjs";
import { useQuery } from "@tanstack/react-query";

const chartConfig = {
  amount: {
    label: "Stan konta",
    color: "#262626",
  },
} satisfies ChartConfig;

export function MainChart() {
  const [padding, setPadding] = useState<{ left: number; right: number }>({
    left: 30,
    right: 30,
  });
  const [showRangePicker, setShowRangePicker] = useState<boolean>(false);
  const [startDate, setStartDate] = useState<Dayjs>(
    dayjs().subtract(30, "day")
  );
  const [endDate, setEndDate] = useState<Dayjs>(dayjs());

  const DashboardService = useDashboardService();

  const {
    data: chartData,
    isLoading,
    isError,
    error,
  } = useQuery({
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

  if (isError) {
    toast.error("Failed to fetch main chart data: " + error.message);
  }

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

  const handleDateChange = (newStartDate: Dayjs, newEndDate: Dayjs) => {
    setStartDate(newStartDate);
    setEndDate(newEndDate);
  };

  if (isLoading || !chartData) {
    return <LoadingSpinner />;
  }

  return (
    <>
      {showRangePicker && (
        <RangePickerPopup
          onClose={() => setShowRangePicker(false)}
          onDateChange={handleDateChange}
        />
      )}
      <Card className="flex flex-col w-full h-full bg-transparent z-10 border-none">
        <CardHeader className="flex justify-between items-center">
          <div>
            <CardTitle className="text-xl">Wykres cashflow</CardTitle>
          </div>
          <div className="w-30">
            <DarkButton
              text={"Zmień daty"}
              onClick={() => setShowRangePicker(!showRangePicker)}
            />
          </div>
        </CardHeader>
        <CardContent className="w-full flex flex-col justify-center items-center overflow-hidden my-0 px-0">
          <ChartContainer
            config={chartConfig}
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
                padding={padding}
                className="font-lato"
              />
              <YAxis
                axisLine={false}
                tickLine={false}
                tickMargin={16}
                className="font-lato"
              />
              <ChartTooltip
                cursor={false}
                content={<ChartTooltipContent className="font-lato" />}
              />
              <Line
                dataKey="amount"
                type="natural"
                stroke="#262626"
                strokeWidth={2}
                dot={false}
              />
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
