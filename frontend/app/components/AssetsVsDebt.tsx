import {
  Card,
  CardAction,
  CardContent,
  CardHeader,
  CardTitle,
} from '@/components/ui/card';
import { ArrowDown, ArrowUp, ArrowUpRight, TrendingUp } from 'lucide-react';
import { NavLink } from 'react-router';
import AssetVsDebtChart from './charts/assetVsDebtChart';
import { buildMockFinance, type Point } from '~/lib/mockFinance';
import { useMemo, useState, useCallback } from 'react';
import type { RangeKey } from 'types';
import { cn } from '~/lib/utils';

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

  const netWorth = displayData.assets - displayData.debt;
  const isPositiveAssets = displayData.assetsChangePct >= 0;
  const isPositiveDebt = displayData.debtChangePct >= 0;

  return (
    <Card className="overflow-hidden">
      {/* Decorative top accent */}
      <div className="h-0.5 bg-gradient-to-r from-transparent via-primary/50 to-transparent" />
      
      <CardHeader className="pb-2">
        <div className="flex items-center gap-2">
          <div className="p-2 rounded-xl bg-chart-1/10">
            <TrendingUp className="w-4 h-4 text-chart-1" />
          </div>
          <CardTitle>Net Worth</CardTitle>
        </div>
        {nav && (
          <CardAction>
            <NavLink 
              to="/accounts" 
              className="group flex items-center gap-1.5 text-xs font-mono text-muted-foreground hover:text-primary transition-colors uppercase tracking-wider"
            >
              Accounts
              <ArrowUpRight className="w-3.5 h-3.5 transition-transform group-hover:-translate-y-0.5 group-hover:translate-x-0.5" />
            </NavLink>
          </CardAction>
        )}
      </CardHeader>
      
      <CardContent className="space-y-6">
        {/* Net Worth Display */}
        <div className="text-center pb-2">
          <p className="text-xs font-mono text-muted-foreground uppercase tracking-wider mb-1">
            Total Net Worth
          </p>
          <p className="text-3xl font-bold font-mono tracking-tight text-gradient-cool">
            ${netWorth.toLocaleString()}
          </p>
        </div>
        
        {/* Assets & Debt Cards */}
        <div className="grid grid-cols-2 gap-4">
          {/* Assets */}
          <div className="p-4 rounded-xl bg-chart-1/5 border border-chart-1/20 transition-all hover:border-chart-1/40">
            <div className="flex items-center gap-2 mb-2">
              <div className="w-2.5 h-2.5 rounded-full bg-chart-1 shadow-lg shadow-chart-1/30" />
              <span className="text-sm font-medium text-muted-foreground">Assets</span>
            </div>
            <p className="text-2xl font-bold font-mono tracking-tight text-foreground mb-1">
              ${displayData.assets.toLocaleString()}
            </p>
            <div className={cn(
              'inline-flex items-center gap-1 px-2 py-0.5 rounded-full text-xs font-mono font-semibold',
              isPositiveAssets 
                ? 'bg-positive/10 text-positive' 
                : 'bg-negative/10 text-negative'
            )}>
              {isPositiveAssets ? (
                <ArrowUp className="w-3 h-3" />
              ) : (
                <ArrowDown className="w-3 h-3" />
              )}
              {Math.abs(displayData.assetsChangePct).toFixed(2)}%
            </div>
          </div>

          {/* Debt */}
          <div className="p-4 rounded-xl bg-chart-2/5 border border-chart-2/20 transition-all hover:border-chart-2/40">
            <div className="flex items-center gap-2 mb-2">
              <div className="w-2.5 h-2.5 rounded-full bg-chart-2 shadow-lg shadow-chart-2/30" />
              <span className="text-sm font-medium text-muted-foreground">Debt</span>
            </div>
            <p className="text-2xl font-bold font-mono tracking-tight text-foreground mb-1">
              ${displayData.debt.toLocaleString()}
            </p>
            <div className={cn(
              'inline-flex items-center gap-1 px-2 py-0.5 rounded-full text-xs font-mono font-semibold',
              !isPositiveDebt 
                ? 'bg-positive/10 text-positive' 
                : 'bg-negative/10 text-negative'
            )}>
              {isPositiveDebt ? (
                <ArrowUp className="w-3 h-3" />
              ) : (
                <ArrowDown className="w-3 h-3" />
              )}
              {Math.abs(displayData.debtChangePct).toFixed(2)}%
            </div>
          </div>
        </div>
        
        {/* Chart */}
        <div className="pt-2">
          <AssetVsDebtChart data={data} onHover={handleHover} />
        </div>
        
        {/* Time Range Selector */}
        <div className="flex justify-center gap-1.5 pt-2">
          {(['1W', '1M', '3M', 'YTD', '1Y', 'ALL'] as RangeKey[]).map((r) => (
            <button
              key={r}
              onClick={() => setRange(r)}
              className={cn(
                'px-3 py-1.5 rounded-lg text-xs font-mono font-semibold transition-all duration-200',
                r === range 
                  ? 'bg-primary text-primary-foreground shadow-lg shadow-primary/20' 
                  : 'text-muted-foreground hover:text-foreground hover:bg-muted/50'
              )}
            >
              {r}
            </button>
          ))}
        </div>
      </CardContent>
    </Card>
  );
}
