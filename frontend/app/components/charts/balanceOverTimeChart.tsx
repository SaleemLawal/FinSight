import React, { memo, useRef } from 'react';
import {
  Line,
  LineChart,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis,
} from 'recharts';
import type { BalanceHistoryPoint } from 'types';
import { format } from 'date-fns';

interface BalanceOverTimeChartProps {
  balanceHistoryPoints: BalanceHistoryPoint[] | undefined;
  onHover?: (data: BalanceHistoryPoint | null) => void;
  allowHover?: boolean;
}

const BalanceOverTimeChart = memo(function BalanceOverTimeChart({
  balanceHistoryPoints,
  onHover,
  allowHover = true,
}: BalanceOverTimeChartProps) {
  const lastIdx = balanceHistoryPoints?.length
    ? balanceHistoryPoints.length - 1
    : 0;

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
          stroke="#6b7280"
          strokeWidth={1}
        />
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

  const CustomTooltip = ({ active, payload }: any) => {
    if (active && payload?.[0]) {
      const point = payload[0]?.payload;

      if (onHover && point) {
        onHover({
          ...point,
        });
      }
    } else if (!active) {
      if (onHover) {
        onHover(null);
      }
    }
    return null;
  };

  return (
    <ResponsiveContainer
      width="100%"
      className="mt-4"
      height={allowHover ? 110 : 25}
    >
      <LineChart data={balanceHistoryPoints} className="outline-none!">
        <XAxis
          dataKey="date"
          tickFormatter={(d: Date) => format(d, 'MMM')}
          hide
        />
        <YAxis hide domain={['auto', 'auto']} />
        {allowHover && (
          <Tooltip
            content={<CustomTooltip />}
            cursor={<VerticalCursorWithDate />}
          />
        )}
        <Line
          type="monotone"
          dataKey="balance"
          stroke="#009624"
          strokeWidth={3}
          dot={
            allowHover
              ? ({ index, cx, cy }) =>
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
              : false
          }
        />
      </LineChart>
    </ResponsiveContainer>
  );
});

export default BalanceOverTimeChart;
