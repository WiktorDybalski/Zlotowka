import formatMoney from "@/utils/formatMoney";
import {CustomLabelProps} from "@/interfaces/dashboard/charts/MainChart";

export default function CustomLabel({ viewBox, total }: CustomLabelProps) {
  const { cx, cy } = viewBox as { cx: number; cy: number };

  if (cx === undefined || cy === undefined) return null;

  return (
      <text x={cx} y={cy} textAnchor="middle" dominantBaseline="middle">
        <tspan
            x={cx}
            y={cy}
            className="text-lg sm:text-xl md:text-2xl font-lato"
        >
          {formatMoney(Number(total))} PLN
        </tspan>
        <tspan x={cx} y={cy + 24} className="text-sm">
          Suma
        </tspan>
      </text>
  );
}