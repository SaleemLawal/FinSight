import {
  Card,
  CardAction,
  CardContent,
  CardHeader,
  CardTitle,
} from '@/components/ui/card';
import { ArrowDown, ArrowUp, MoveUpRight } from 'lucide-react';
import { NavLink } from 'react-router';
import AssetVsDebtChart from './charts/assetVsDebtChart';
import { buildMockFinance, type Point } from '~/lib/mockFinance';
import { useMemo, useState, useCallback } from 'react';
import { Badge } from './ui/badge';
import type { RangeKey } from 'types';
import { cn, switchBadgeColor } from '~/lib/utils';

const { ranges, kpis } = buildMockFinance();

export default function AssetsVsDebt({
  nav = true,
  range,
  setRange,
}: {
  nav?: boolean;
  range: RangeKey;
  setRange: (range: RangeKey) => void;
}) {
  const [hoveredData, setHoveredData] = useState<
    (Point & { assetsChangePct: number; debtChangePct: number }) | null
  >(null);

  const data = useMemo(() => ranges[range], [range]);
  const rangeKpis = useMemo(() => kpis[range], [range]);

  const displayData = hoveredData || {
    assets: rangeKpis.assetsNow,
    debt: rangeKpis.debtNow,
    assetsChangePct: rangeKpis.assetsChangePct,
    debtChangePct: rangeKpis.debtChangePct,
  };

  const handleHover = useCallback(
    (
      point: (Point & { assetsChangePct: number; debtChangePct: number }) | null
    ) => {
      setHoveredData(point);
    },
    []
  );

  return (
    <Card>
      <CardHeader>
        <CardTitle>Accounts</CardTitle>
        {nav && (
          <CardAction className="text-xs text-muted-foreground">
            <NavLink to="/accounts" className="flex items-center gap-1">
              ACCOUNTS
              <MoveUpRight className="w-4 h-4" />
            </NavLink>
          </CardAction>
        )}
      </CardHeader>
      <CardContent>
        <div className="flex gap-10 justify-center">
          <div>
            <div className="flex items-center gap-2">
              <span className="w-2 h-2 rounded-full bg-[#6fa8ff]" />
              <span className="text-muted-foreground">Assets</span>
            </div>
            <div className="text-lg font-semibold">
              ${displayData.assets.toLocaleString()}
            </div>
            <Badge
              className={cn(
                'inline-flex items-center gap-1 py-0.5 px-2 rounded-full text-sm font-semibold mt-1',
                switchBadgeColor(displayData.assetsChangePct)
              )}
            >
              {displayData.assetsChangePct > 0 ? <ArrowUp /> : <ArrowDown />}{' '}
              {Math.abs(displayData.assetsChangePct).toFixed(2)}%
            </Badge>
          </div>

          <div>
            <div className="flex items-center gap-2">
              <span className="w-2 h-2 rounded-full bg-[#ff9264]" />
              <span className="text-muted-foreground">Debt</span>
            </div>
            <div className="text-lg font-semibold">
              ${displayData.debt.toLocaleString()}
            </div>
            <Badge
              className={cn(
                'inline-flex items-center gap-1 py-0.5 px-2 rounded-full text-sm font-semibold mt-1',
                switchBadgeColor(displayData.debtChangePct)
              )}
            >
              {displayData.debtChangePct > 0 ? <ArrowUp /> : <ArrowDown />}{' '}
              {Math.abs(displayData.debtChangePct).toFixed(2)}%
            </Badge>
          </div>
        </div>
        <AssetVsDebtChart data={data} onHover={handleHover} />
        <div className="flex justify-center gap-3 mt-3">
          {(['1W', '1M', '3M', 'YTD', '1Y', 'ALL'] as RangeKey[]).map((r) => (
            <button
              key={r}
              onClick={() => setRange(r)}
              className={`px-3 py-1 rounded-full text-sm ${
                r === range ? 'bg-[#16315c] text-white' : ''
              }`}
            >
              {r}
            </button>
          ))}
        </div>
      </CardContent>
    </Card>
  );
}
