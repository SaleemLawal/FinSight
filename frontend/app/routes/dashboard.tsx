import AssetsVsDebt from '~/components/AssetsVsDebt';
import MonthlySpending from '~/components/monthlySpending';
import type { Route } from './+types/dashboard';
import DashboardHeader from '~/components/headers/dashboardHeader';
import { useState } from 'react';
import type { RangeKey } from 'types';

export function meta({}: Route.MetaArgs) {
  return [{ title: 'FinSight' }, { name: 'description', content: 'FinSight' }];
}

export default function Home() {
  const [range, setRange] = useState<RangeKey>('1W');

  return (
    <>
      <DashboardHeader />
      <div className="p-8">
        <div className="grid grid-cols-2 gap-4">
          <MonthlySpending />
          <AssetsVsDebt range={range} setRange={setRange} />
        </div>
      </div>
    </>
  );
}
