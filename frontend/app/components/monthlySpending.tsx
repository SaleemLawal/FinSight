import {
  Card,
  CardAction,
  CardContent,
  CardHeader,
  CardTitle,
} from '@/components/ui/card';
import { MoveUpRight } from 'lucide-react';
import { NavLink } from 'react-router';
import SpendingChart from './charts/spendingChart';

export default function MonthlySpending() {
  return (
    <Card>
      <CardHeader>
        <CardTitle>Monthly Spending</CardTitle>
        <CardAction className="text-xs text-muted-foreground">
          <NavLink to="/transactions" className="flex items-center gap-1">
            TRANSACTIONS
            <MoveUpRight className="w-4 h-4" />
          </NavLink>
        </CardAction>
      </CardHeader>
      <CardContent>
        <div className="flex items-center flex-col">
          <p className="text-sm text-card-foreground">
            <span className="font-bold text-lg">$500 left</span>
          </p>
          <p className="text-sm text-muted-foreground">out of $7,000 budget</p>
        </div>
        <SpendingChart />
      </CardContent>
    </Card>
  );
}
