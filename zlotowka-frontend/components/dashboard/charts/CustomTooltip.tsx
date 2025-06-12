import dayjs from "dayjs";
import { CustomChartTooltipProps } from "@/interfaces/dashboard/charts/MainChart";

export default function CustomChartTooltip({
  active,
  payload,
  label,
  transactions,
}: CustomChartTooltipProps) {
  if (!active || !payload || payload.length === 0 || !label) {
    return null;
  }

  const hoveredDate = dayjs(label).format("YYYY-MM-DD");

  const dayTransactions = transactions
    .filter((tx) => tx.date === hoveredDate)
    .sort((a, b) => a.amount - b.amount);

  return (
    <div className="bg-background dark:bg-accent  p-2 rounded shadow-md text-sm font-lato">
      {dayTransactions.length > 0 ? (
        <>
          <div className="font-bold mb-1">Data: {dayTransactions[0].date}</div>
          <div className="font-bold mb-1">
            Stan konta: {payload[0].value} PLN
          </div>
          <ul className="mt-1 space-y-1">
            {dayTransactions.map((tx) => (
              <li key={tx.transactionId}>
                <span
                  className={`text-xs ${
                    tx.isIncome ? "text-green-600" : "text-[#c82026]"
                  }`}
                >
                  {tx.isIncome ? "+" : "-"}
                  {tx.amount} {tx.currency.isoCode}
                </span>{" "}
                – {tx.name}
              </li>
            ))}
          </ul>
        </>
      ) : (
        <>
          <div className="font-bold mb-1">Data: {hoveredDate}</div>
          <div className="font-bold">Stan konta: {payload[0].value} PLN</div>
        </>
      )}
    </div>
  );
}
