// AssetsVsDebtCardChart.tsx
import {
    ResponsiveContainer,
    LineChart,
    Line,
    XAxis,
    YAxis,
    Tooltip,
    ReferenceLine,
} from "recharts";
import { format } from "date-fns";

type Point = { date: Date; assets: number; debt: number };

const MiniTooltip = ({ active, payload, label }: any) => {
    if (!active || !payload?.length) return null;
    return (
        <div className="pointer-events-none text-xs font-mono space-y-1">
            <div className="text-slate-400 font-bold">{format(label, "MMM d")}</div>
            {payload.map((p: any) => (
                <div key={p.dataKey} className="flex justify-between gap-2">
                    <span className="flex items-center gap-1">
                        <span
                            className="inline-block w-1.5 h-1.5 rounded-full"
                            style={{ background: p.color }}
                        />
                        {p.dataKey === "assets" ? "Assets" : "Debt"}
                    </span>
                    <span>${p.value.toLocaleString()}</span>
                </div>
            ))}
        </div>
    );
};

export default function AssetsVsDebtCardChart({ data }: { data: Point[] }) {

    const lastIdx = data.length - 1;

    return (
        <>
            <ResponsiveContainer width="100%" height={110}>
                <LineChart data={data}>
                    <XAxis
                        dataKey="date"
                        tickFormatter={(d: Date) => format(d, "MMM")}
                        hide
                    />
                    <YAxis hide domain={["auto", "auto"]} />
                    <ReferenceLine
                        ifOverflow="extendDomain"
                        x={data[lastIdx]?.date}
                        stroke="#444c55"
                        strokeWidth={2}
                        strokeDasharray="1 6"
                        isFront
                    />
                    <Tooltip content={<MiniTooltip />} cursor={false} />

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
                            ) : null
                        }
                        activeDot={false}
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
                            ) : null
                        }
                        activeDot={false}
                    />
                </LineChart>
            </ResponsiveContainer>
        </>
    );
}
