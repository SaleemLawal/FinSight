import {
    Card,
    CardAction,
    CardContent,
    CardHeader,
    CardTitle,
} from "@/components/ui/card"
import { MoveUpRight } from "lucide-react"
import { NavLink } from "react-router"
import AssetVsDebtChart from "./assetVsDebtChart"
import { buildMockFinance, type RangeKey } from "~/lib/mockFinance"
import { useMemo, useState } from "react"

const { ranges, kpis } = buildMockFinance();

export default function AssetsVsDebt() {
    const [range, setRange] = useState<RangeKey>("1M");
    const data = useMemo(() => ranges[range], [range]);
    const { assetsNow, debtNow, assetsChangePct, debtChangePct } = kpis[range];

    return (
        <Card>
            <CardHeader>
                <CardTitle>Accounts</CardTitle>
                <CardAction className="text-xs text-muted-foreground">
                    <NavLink to="/transactions" className="flex items-center gap-1">
                        ACCOUNTS
                        <MoveUpRight className="w-4 h-4" />
                    </NavLink>
                </CardAction>
            </CardHeader>
            <CardContent>
                <div className="flex gap-10 justify-center">
                    <div>
                        <div className="flex items-center gap-2">
                            <span className="w-2 h-2 rounded-full bg-[#6fa8ff]" />
                            <span className="text-muted-foreground">Assets</span>
                        </div>
                        <div className="text-lg font-semibold">
                            ${assetsNow.toLocaleString()}
                        </div>
                        <div className="bg-green-600/20 text-green-400 inline-flex items-center gap-1 py-0.5 px-2 rounded-full text-sm font-semibold mt-1">
                            ↑ {assetsChangePct.toFixed(2)}%
                        </div>
                    </div>

                    <div>
                        <div className="flex items-center gap-2">
                            <span className="w-2 h-2 rounded-full bg-[#ff9264]" />
                            <span className="text-muted-foreground">Debt</span>
                        </div>
                        <div className="text-lg font-semibold">
                            ${debtNow.toLocaleString()}
                        </div>
                        <div className="bg-green-600/20 text-green-400 inline-flex items-center gap-1 py-0.5 px-2 rounded-full text-sm font-semibold mt-1">
                            ↓ {Math.abs(debtChangePct).toFixed(2)}%
                        </div>
                    </div>
                </div>
                <AssetVsDebtChart data={data} />
                <div className="flex justify-center gap-3 mt-3">
                    {(["1W", "1M", "3M", "YTD", "1Y", "ALL"] as RangeKey[]).map((r) => (
                        <button
                            key={r}
                            onClick={() => setRange(r)}
                            className={`px-3 py-1 rounded-full text-sm ${r === range
                                ? "bg-[#16315c] text-white"
                                : ""
                                }`}
                        >
                            {r}
                        </button>
                    ))}
                </div>
            </CardContent>

        </Card>
    )
}
