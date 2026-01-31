import { LineChart, Line, XAxis, YAxis, ResponsiveContainer } from 'recharts';

const MONTHLY_BUDGET = 8000;
const DAYS_IN_MONTH = 30;
const DAILY_BUDGET_RATE = MONTHLY_BUDGET / DAYS_IN_MONTH;

// Modern color palette for spending ratio
const BG_COLOR = '#1a1d24';
const EXPECTED_COLOR = '#3a3f4b';

// Modern gradient colors - muted, sophisticated
const COLOR_GREAT = '#22c55e';    // Green - well under budget
const COLOR_GOOD = '#4ade80';     // Light green
const COLOR_OK = '#facc15';       // Yellow - at budget
const COLOR_WARNING = '#f97316';  // Orange - slightly over
const COLOR_OVER = '#ef4444';     // Red - over budget

// Convert ratio to modern color
function ratioToColor(ratio: number): string {
  if (ratio <= 0.7) return COLOR_GREAT;
  if (ratio <= 0.85) return COLOR_GOOD;
  if (ratio <= 1.0) return COLOR_OK;
  if (ratio <= 1.15) return COLOR_WARNING;
  return COLOR_OVER;
}

const rawSpending = [
  { dayNumber: 1, spent: 0 },
  { dayNumber: 2, spent: 400 },
  { dayNumber: 3, spent: 900 },
  { dayNumber: 4, spent: 1300 },
  { dayNumber: 5, spent: 1800 },
  { dayNumber: 6, spent: 2000 },
  { dayNumber: 7, spent: 2400 },
  { dayNumber: 8, spent: 2700 },
  { dayNumber: 9, spent: 3000 },
  { dayNumber: 10, spent: 3300 },
  { dayNumber: 11, spent: 3700 },
  { dayNumber: 12, spent: 4000 },
  { dayNumber: 13, spent: 4200 },
  { dayNumber: 14, spent: 4400 },
  { dayNumber: 15, spent: 4600 },
  { dayNumber: 16, spent: 4700 },
  { dayNumber: 17, spent: 4900 },
  { dayNumber: 18, spent: 5100 },
  { dayNumber: 19, spent: 5250 },
  { dayNumber: 20, spent: 5400 },
  { dayNumber: 21, spent: 5700 },
  { dayNumber: 22, spent: 5900 },
  { dayNumber: 23, spent: 6000 },
  { dayNumber: 24, spent: 6150 },
  { dayNumber: 25, spent: 6200 },
  { dayNumber: 26, spent: 6250 },
  { dayNumber: 27, spent: 6300 },
  { dayNumber: 28, spent: 6400 },
  { dayNumber: 29, spent: 6450 },
  { dayNumber: 30, spent: 6550 },
];

const data = Array.from({ length: DAYS_IN_MONTH }, (_, i) => {
  const dayNumber = i + 1;
  const expected = DAILY_BUDGET_RATE * dayNumber;
  const actual =
    rawSpending.find((d) => d.dayNumber === dayNumber)?.spent ?? null;
  const ratio = actual != null ? actual / expected : null;
  return {
    day: `${dayNumber}`,
    dayNumber,
    spent: actual,
    expectedSpending: expected,
    color: ratio != null ? ratioToColor(ratio) : null,
  };
});

const realised = data.filter((d) => d.spent != null);
const gradientStops = realised.map((pt, idx) => ({
  offset: `${(idx / (realised.length - 1)) * 100}%`,
  color: pt.color!,
}));

const CURRENT_DAY_INDEX = realised.length - 1;
const latestPoint = realised[CURRENT_DAY_INDEX];

export default function SpendingChart() {
  return (
    <ResponsiveContainer width="100%" height={130}>
      <LineChart data={data} margin={{ top: 10, right: 15, left: 10, bottom: 10 }}>
        <defs>
          <linearGradient id="spendingGradient" x1="0" y1="0" x2="1" y2="0">
            {gradientStops.map((s, i) => (
              <stop key={i} offset={s.offset} stopColor={s.color} />
            ))}
          </linearGradient>
        </defs>

        <XAxis dataKey="day" hide />
        <YAxis
          hide
          domain={[0, Math.max(MONTHLY_BUDGET, latestPoint.spent!) * 1.1]}
        />

        {/* Expected spending - subtle dashed line */}
        <Line
          type="monotone"
          dataKey="expectedSpending"
          stroke={EXPECTED_COLOR}
          strokeWidth={1.5}
          strokeDasharray="3 6"
          dot={false}
          isAnimationActive={false}
        />

        {/* Actual spending - gradient line */}
        <Line
          type="monotone"
          dataKey="spent"
          stroke="url(#spendingGradient)"
          strokeWidth={2.5}
          strokeLinecap="round"
          connectNulls={false}
          isAnimationActive={false}
          dot={({ index, cx, cy }) =>
            index === CURRENT_DAY_INDEX ? (
              <g>
                {/* Outer ring */}
                <circle
                  cx={cx}
                  cy={cy}
                  r={6}
                  fill="none"
                  stroke={latestPoint.color ?? undefined}
                  strokeWidth={2}
                  opacity={0.3}
                />
                {/* Inner filled circle */}
                <circle
                  cx={cx}
                  cy={cy}
                  r={4}
                  fill={latestPoint.color ?? undefined}
                />
              </g>
            ) : (
              <></>
            )
          }
        />
      </LineChart>
    </ResponsiveContainer>
  );
}
