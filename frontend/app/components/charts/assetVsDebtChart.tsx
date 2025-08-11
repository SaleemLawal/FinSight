// AssetsVsDebtCardChart.tsx
import {
  ResponsiveContainer,
  LineChart,
  Line,
  XAxis,
  YAxis,
  Tooltip,
  ReferenceLine,
} from 'recharts';
import { format } from 'date-fns';
import { memo } from 'react';

type Point = { date: Date; assets: number; debt: number };

interface ChartProps {
  data: Point[];
  onHover: (
    data: (Point & { assetsChangePct: number; debtChangePct: number }) | null
  ) => void;
}

// const MiniTooltip = ({ active, payload, label }: any) => {
//     if (!active || !payload?.length) return null;
//     return (
//         <div className="text-xs font-mono space-y-1">
//             <div className="text-slate-400 font-bold">{format(label, "MMM d")}</div>
//             {payload.map((p: any) => (
//                 <div key={p.dataKey} className="flex justify-between gap-2">
//                     <span className="flex items-center gap-1">
//                         <span
//                             className="inline-block w-1.5 h-1.5 rounded-full"
//                             style={{ background: p.color }}
//                         />
//                         {p.dataKey === "assets" ? "Assets" : "Debt"}
//                     </span>
//                     <span>${p.value.toLocaleString()}</span>
//                 </div>
//             ))}
//         </div>
//     );
// };

const VerticalCursorWithDate = ({ points, height, payload }: any) => {
  if (!points || points.length === 0 || !payload?.[0]) return null;

  const x = points[0]?.x;
  const date = payload[0]?.payload?.date;

  if (x === undefined || !date) return null;

  return (
    <g>
      <line x1={x} x2={x} y1={0} y2={height} stroke="#6b7280" strokeWidth={1} />
      <text
        x={x + 20}
        y={height - 90}
        textAnchor="middle"
        fill="#6b7280"
        fontSize="10"
        fontFamily="monospace"
      >
        {format(date, 'MMM d')}
      </text>
    </g>
  );
};

const AssetsVsDebtCardChart = memo(function AssetsVsDebtCardChart({
  data,
  onHover,
}: ChartProps) {
  const lastIdx = data.length - 1;

  const CustomTooltip = ({ active, payload }: any) => {
    if (active && payload?.[0]) {
      const point = payload[0].payload;
      // const currentIndex = data.findIndex(d => d.date.getTime() === point.date.getTime());

      const firstPoint = data[0];
      const assetsChangePct =
        firstPoint && firstPoint.assets !== 0
          ? ((point.assets - firstPoint.assets) / firstPoint.assets) * 100
          : 0;
      const debtChangePct =
        firstPoint && firstPoint.debt !== 0
          ? ((point.debt - firstPoint.debt) / firstPoint.debt) * 100
          : 0;

      onHover({
        ...point,
        assetsChangePct,
        debtChangePct,
      });
    } else if (!active) {
      onHover(null);
    }
    return null;
  };

  return (
    <>
      <ResponsiveContainer width="100%" height={110}>
        <LineChart data={data}>
          <XAxis
            dataKey="date"
            tickFormatter={(d: Date) => format(d, 'MMM')}
            hide
          />
          <YAxis hide domain={['auto', 'auto']} />
          {/* <ReferenceLine
                        ifOverflow="extendDomain"
                        stroke="#444c55"
                        strokeWidth={2}
                        strokeDasharray="1 6"
                    /> */}
          <Tooltip
            content={<CustomTooltip />}
            cursor={<VerticalCursorWithDate />}
          />

          <Line
            type="monotone"
            dataKey="debt"
            stroke="#ff9264"
            strokeWidth={3}
            dot={({ index, cx, cy }) =>
              index === lastIdx ? (
                <circle
                  cx={cx}
                  cy={cy}
                  r={6}
                  fill="#0d1117"
                  stroke="#ff9264"
                  strokeWidth={2}
                />
              ) : (
                <></>
              )
            }
            activeDot
          />

          <Line
            type="monotone"
            dataKey="assets"
            stroke="#6fa8ff"
            strokeWidth={3}
            dot={({ index, cx, cy }) =>
              index === lastIdx ? (
                <circle
                  cx={cx}
                  cy={cy}
                  r={6}
                  fill="#0d1117"
                  stroke="#6fa8ff"
                  strokeWidth={2}
                />
              ) : (
                <></>
              )
            }
            activeDot
          />
        </LineChart>
      </ResponsiveContainer>
    </>
  );
});

export default AssetsVsDebtCardChart;
