import AssetsVsDebt from '~/components/AssetsVsDebt';
import MonthlySpending from '~/components/monthlySpending';
import type { Route } from './+types/dashboard';
import DashboardHeader from '~/components/headers/dashboardHeader';
import { useState } from 'react';
import type { RangeKey } from 'types';

export function meta({}: Route.MetaArgs) {
  return [{ title: 'FinSight | Financial Command Center' }, { name: 'description', content: 'Your personal financial command center' }];
}

export default function Home() {
  const [range, setRange] = useState<RangeKey>('1W');

  return (
    <div className="min-h-screen bg-background">
      <DashboardHeader />
      
      {/* Main Content */}
      <main className="p-6">
        {/* Dashboard Grid */}
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6 items-stretch">
          <div className="animate-fade-in-up stagger-1 flex">
            <MonthlySpending />
          </div>
          <div className="animate-fade-in-up stagger-2 flex">
            <AssetsVsDebt range={range} setRange={setRange} />
          </div>
        </div>
      </main>
    </div>
  );
}
