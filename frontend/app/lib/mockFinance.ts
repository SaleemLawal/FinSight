// mockFinance.ts
import { subDays, startOfYear } from 'date-fns';
import type { RangeKey } from 'types';

export type Point = { date: Date; assets: number; debt: number };

export type RangeBuckets = Record<RangeKey, Point[]>;

export interface Kpis {
  assetsNow: number;
  debtNow: number;
  assetsChangePct: number;
  debtChangePct: number;
}

const rand = (min: number, max: number) => Math.random() * (max - min) + min;

export function generateFinanceSeries(
  days: number,
  startAssets = 10000,
  startDebt = 17000
): Point[] {
  const data: Point[] = [];
  let assets = startAssets;
  let debt = startDebt;
  const today = new Date();

  for (let i = days - 1; i >= 0; i--) {
    const date = subDays(today, i);

    const aDrift = rand(0.02, 0.25);
    const aNoise = rand(-40, 40);
    assets = Math.max(0, assets + aDrift + aNoise * 100);
    const dDrift = rand(-0.3, -0.05);
    const dNoise = rand(-30, 30);
    debt = Math.max(0, debt + dDrift + dNoise);

    data.push({
      date,
      assets: Math.round(assets),
      debt: Math.round(debt),
    });
  }

  return data;
}

export function sliceByRange(all: Point[], range: RangeKey): Point[] {
  const now = all[all.length - 1]?.date ?? new Date();
  switch (range) {
    case '1W':
      return all.filter((d) => d.date >= subDays(now, 7));
    case '1M':
      return all.filter((d) => d.date >= subDays(now, 30));
    case '3M':
      return all.filter((d) => d.date >= subDays(now, 90));
    case 'YTD':
      return all.filter((d) => d.date >= startOfYear(now));
    case '1Y':
      return all.filter((d) => d.date >= subDays(now, 365));
    case 'ALL':
    default:
      return all;
  }
}

export function computeKpis(series: Point[]): Kpis {
  const first = series[0];
  const last = series[series.length - 1];
  const assetsChangePct =
    first && first.assets !== 0
      ? ((last.assets - first.assets) / first.assets) * 100
      : 0;
  const debtChangePct =
    first && first.debt !== 0
      ? ((last.debt - first.debt) / first.debt) * 100
      : 0;

  return {
    assetsNow: last.assets,
    debtNow: last.debt,
    assetsChangePct,
    debtChangePct,
  };
}

export function buildMockFinance(): {
  all: Point[];
  ranges: RangeBuckets;
  kpis: Record<RangeKey, Kpis>;
} {
  const all = generateFinanceSeries(730);
  const ranges: RangeBuckets = {
    ALL: all,
    '1Y': sliceByRange(all, '1Y'),
    '3M': sliceByRange(all, '3M'),
    '1M': sliceByRange(all, '1M'),
    '1W': sliceByRange(all, '1W'),
    YTD: sliceByRange(all, 'YTD'),
  };

  const kpis = Object.fromEntries(
    (Object.keys(ranges) as RangeKey[]).map((key) => [
      key,
      computeKpis(ranges[key]),
    ])
  ) as Record<RangeKey, Kpis>;

  return { all, ranges, kpis };
}
