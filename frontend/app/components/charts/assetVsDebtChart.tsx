// AssetsVsDebtCardChart.tsx
import {
  ResponsiveContainer,
  LineChart,
  Line,
  XAxis,
  YAxis,
  Tooltip,
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

// Chart colors matching theme
const ASSETS_COLOR = 'oklch(0.65 0.15 220)';  // Blue
const DEBT_COLOR = 'oklch(0.65 0.15 30)';     // Orange
const CURSOR_COLOR = 'oklch(0.45 0.01 260)';  // Muted gray
const BG_COLOR = 'oklch(0.18 0.005 260)';     // Card background

const VerticalCursorWithDate = ({ points, height, payload }: any) => {
  if (!points || points.length === 0 || !payload?.[0]) return null;

  const x = points[0]?.x;
  const date = payload[0]?.payload?.date;

  if (x === undefined || !date) return null;

  return (
    <g>
      <line 
        x1={x} 
        x2={x} 
        y1={0} 
        y2={height} 
        stroke={CURSOR_COLOR} 
        strokeWidth={1} 
        strokeDasharray="4 4"
      />
      <rect
        x={x - 24}
        y={height - 24}
        width={48}
        height={18}
        rx={4}
        fill={BG_COLOR}
        stroke={CURSOR_COLOR}
        strokeWidth={1}
      />
      <text
        x={x}
        y={height - 11}
        textAnchor="middle"
        fill="oklch(0.7 0.02 250)"
        fontSize="10"
        fontFamily="IBM Plex Mono, monospace"
        fontWeight="500"
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
    <div className="relative">
      <ResponsiveContainer width="100%" height={130}>
        <LineChart data={data} margin={{ top: 10, right: 10, left: 10, bottom: 10 }}>
          <defs>
            <linearGradient id="assetsGradient" x1="0" y1="0" x2="0" y2="1">
              <stop offset="0%" stopColor={ASSETS_COLOR} stopOpacity={0.3} />
              <stop offset="100%" stopColor={ASSETS_COLOR} stopOpacity={0} />
            </linearGradient>
            <linearGradient id="debtGradient" x1="0" y1="0" x2="0" y2="1">
              <stop offset="0%" stopColor={DEBT_COLOR} stopOpacity={0.3} />
              <stop offset="100%" stopColor={DEBT_COLOR} stopOpacity={0} />
            </linearGradient>
            {/* Glow filter */}
            <filter id="glow" x="-50%" y="-50%" width="200%" height="200%">
              <feGaussianBlur stdDeviation="3" result="coloredBlur" />
              <feMerge>
                <feMergeNode in="coloredBlur" />
                <feMergeNode in="SourceGraphic" />
              </feMerge>
            </filter>
          </defs>

          <XAxis
            dataKey="date"
            tickFormatter={(d: Date) => format(d, 'MMM')}
            hide
          />
          <YAxis hide domain={['auto', 'auto']} />

          <Tooltip
            content={<CustomTooltip />}
            cursor={<VerticalCursorWithDate />}
          />

          <Line
            type="monotone"
            dataKey="debt"
            stroke={DEBT_COLOR}
            strokeWidth={2.5}
            dot={({ index, cx, cy }) =>
              index === lastIdx ? (
                <g filter="url(#glow)">
                  <circle
                    cx={cx}
                    cy={cy}
                    r={5}
                    fill={BG_COLOR}
                    stroke={DEBT_COLOR}
                    strokeWidth={2}
                  />
                </g>
              ) : (
                <></>
              )
            }
            activeDot={{
              r: 5,
              fill: BG_COLOR,
              stroke: DEBT_COLOR,
              strokeWidth: 2,
            }}
          />

          <Line
            type="monotone"
            dataKey="assets"
            stroke={ASSETS_COLOR}
            strokeWidth={2.5}
            dot={({ index, cx, cy }) =>
              index === lastIdx ? (
                <g filter="url(#glow)">
                  <circle
                    cx={cx}
                    cy={cy}
                    r={5}
                    fill={BG_COLOR}
                    stroke={ASSETS_COLOR}
                    strokeWidth={2}
                  />
                </g>
              ) : (
                <></>
              )
            }
            activeDot={{
              r: 5,
              fill: BG_COLOR,
              stroke: ASSETS_COLOR,
              strokeWidth: 2,
            }}
          />
        </LineChart>
      </ResponsiveContainer>
    </div>
  );
});

export default AssetsVsDebtCardChart;
