import AssetsVsDebt from '~/components/graphs/AssetsVsDebt';
import MonthlySpending from '~/components/graphs/monthlySpending';
import type { Route } from './+types/home';

export function meta({}: Route.MetaArgs) {
  return [{ title: 'FinSight' }, { name: 'description', content: 'FinSight' }];
}

export default function Home() {
  return (
    <div>
      <div className='grid grid-cols-2 gap-4'>
        <MonthlySpending />
        <AssetsVsDebt />
      </div>
    </div>
  );
}
