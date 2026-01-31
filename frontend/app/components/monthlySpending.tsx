import {
  Card,
  CardAction,
  CardContent,
  CardHeader,
  CardTitle,
} from '@/components/ui/card';
import { ArrowUpRight, Wallet } from 'lucide-react';
import { NavLink } from 'react-router';
import SpendingChart from './charts/spendingChart';

export default function MonthlySpending() {
  const spent = 6500;
  const budget = 7000;
  const remaining = budget - spent;
  const percentUsed = (spent / budget) * 100;

  return (
    <Card className="overflow-hidden">
      {/* Decorative top accent */}
      <div className="h-0.5 bg-gradient-to-r from-transparent via-primary/50 to-transparent" />
      
      <CardHeader className="pb-2">
        <div className="flex items-center gap-2">
          <div className="p-2 rounded-xl bg-primary/10">
            <Wallet className="w-4 h-4 text-primary" />
          </div>
          <CardTitle>Monthly Spending</CardTitle>
        </div>
        <CardAction>
          <NavLink 
            to="/transactions" 
            className="group flex items-center gap-1.5 text-xs font-mono text-muted-foreground hover:text-primary transition-colors uppercase tracking-wider"
          >
            Transactions
            <ArrowUpRight className="w-3.5 h-3.5 transition-transform group-hover:-translate-y-0.5 group-hover:translate-x-0.5" />
          </NavLink>
        </CardAction>
      </CardHeader>
      
      <CardContent className="space-y-6">
        {/* Budget Display */}
        <div className="text-center space-y-2">
          <div className="flex items-baseline justify-center gap-1">
            <span className="text-4xl font-bold font-mono tracking-tight text-gradient">
              ${remaining.toLocaleString()}
            </span>
            <span className="text-sm text-muted-foreground font-medium">left</span>
          </div>
          <p className="text-sm text-muted-foreground">
            out of{' '}
            <span className="font-mono font-semibold text-foreground/80">
              ${budget.toLocaleString()}
            </span>{' '}
            budget
          </p>
          
          {/* Progress indicator */}
          <div className="flex items-center justify-center gap-3 pt-2">
            <div className="flex-1 max-w-48 h-2 rounded-full bg-muted overflow-hidden">
              <div 
                className="h-full rounded-full bg-gradient-to-r from-chart-5 via-chart-3 to-chart-2 transition-all duration-1000 ease-out"
                style={{ width: `${percentUsed}%` }}
              />
            </div>
            <span className="text-xs font-mono text-muted-foreground">
              {percentUsed.toFixed(0)}%
            </span>
          </div>
        </div>
        
        {/* Chart */}
        <div className="pt-2">
          <SpendingChart />
        </div>
        
        {/* Quick stats */}
        <div className="grid grid-cols-2 gap-4 pt-2">
          <div className="p-3 rounded-xl bg-muted/30 border border-border/30">
            <p className="text-xs font-mono text-muted-foreground uppercase tracking-wider mb-1">
              Daily Average
            </p>
            <p className="text-lg font-bold font-mono">
              ${Math.round(spent / 30).toLocaleString()}
            </p>
          </div>
          <div className="p-3 rounded-xl bg-muted/30 border border-border/30">
            <p className="text-xs font-mono text-muted-foreground uppercase tracking-wider mb-1">
              Days Left
            </p>
            <p className="text-lg font-bold font-mono">
              {30 - new Date().getDate()}
            </p>
          </div>
        </div>
      </CardContent>
    </Card>
  );
}
