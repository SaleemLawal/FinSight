import { Badge } from '~/components/ui/badge';
import type {
  Account,
  BalanceHistory,
  BalanceHistoryPoint,
  Transaction,
} from 'types';
import { Avatar, AvatarFallback, AvatarImage } from './ui/avatar';
import { Minus, Plus } from 'lucide-react';
import { cn, formatCurrency, switchBadgeColor } from '~/lib/utils';
import { useCallback, useEffect, useState } from 'react';
import { fetchAccountBalanceHistory } from '~/lib/api/accounts';
import type { RangeKey } from 'types';
import BalanceOverTimeChart from './charts/balanceOverTimeChart';
import { Separator } from './ui/separator';
import TransactionsTable from './TransactionsTable';

export default function AccountDetail({
  account,
  transactions,
}: {
  account: Account;
  transactions: Transaction[];
}) {
  const [range, setRange] = useState<RangeKey>('3M');
  const [hoveredData, setHoveredData] = useState<BalanceHistoryPoint | null>(
    null
  );
  const [balanceHistory, setBalanceHistory] = useState<
    BalanceHistory | undefined
  >(undefined);

  useEffect(() => {
    const fetchBalanceHistory = async () => {
      const balanceHistory: BalanceHistory = await fetchAccountBalanceHistory(
        account.id,
        range
      );
      setBalanceHistory(balanceHistory);
    };
    fetchBalanceHistory();
  }, [account.id, range]);

  const displayData = hoveredData || {
    balance: balanceHistory?.points[balanceHistory?.points.length - 1]?.balance,
    date: balanceHistory?.points[balanceHistory?.points.length - 1]?.date,
  };

  const handleHover = useCallback((point: BalanceHistoryPoint | null) => {
    setHoveredData(point);
  }, []);

  let dollarChangeInBalance = 0;
  // if original poit, show change from first to last
  // else show change from first to hovered
  if (hoveredData == null) {
    dollarChangeInBalance =
      (balanceHistory?.points[balanceHistory?.points.length - 1]?.balance ??
        0) - (balanceHistory?.points[0]?.balance ?? 0);
  } else {
    dollarChangeInBalance =
      hoveredData.balance - (balanceHistory?.points[0]?.balance ?? 0);
  }

  // same for percentage change
  let percentageChangeInBalance = 0;
  if (hoveredData == null) {
    percentageChangeInBalance =
      ((balanceHistory?.points[balanceHistory?.points.length - 1]?.balance ??
        0) -
        (balanceHistory?.points[0]?.balance ?? 0)) /
      (balanceHistory?.points[0]?.balance ?? 0);
  } else {
    percentageChangeInBalance =
      (hoveredData.balance - (balanceHistory?.points[0]?.balance ?? 0)) /
      (balanceHistory?.points[0]?.balance ?? 0);
  }
  percentageChangeInBalance = percentageChangeInBalance * 100;

  return (
    <div>
      <div className="flex justify-between items-center">
        <div className="flex items-center gap-2">
          <Avatar>
            <AvatarImage src="https://github.com/shadcn.png" />
            <AvatarFallback>CN</AvatarFallback>
          </Avatar>

          <p className="text-sm font-semibold">{account.last_four}</p>
          <p className="text-muted-foreground text-xs">Updated 1 day ago</p>
        </div>

        <div className="flex items-center gap-2">
          <Badge
            className={cn(
              'inline-flex items-center gap-1 py-0.5 px-2 rounded-full text-sm font-semibold mt-1',
              switchBadgeColor(dollarChangeInBalance)
            )}
          >
            {dollarChangeInBalance > 0 ? <Plus /> : <Minus />}{' '}
            <span>{formatCurrency(Math.abs(dollarChangeInBalance))}</span>
          </Badge>

          <Badge
            className={cn(
              'inline-flex items-center gap-1 py-0.5 px-2 rounded-full text-sm font-semibold mt-1',
              switchBadgeColor(percentageChangeInBalance)
            )}
          >
            {percentageChangeInBalance > 0 ? <Plus /> : <Minus />}{' '}
            <span>{Math.abs(percentageChangeInBalance).toFixed(2)}%</span>
          </Badge>
        </div>
      </div>
      <div className="flex justify-between items-center mt-2">
        <p className="text-lg font-semibold">{account.name}</p>
        <p className="text-lg font-semibold">
          {formatCurrency(displayData.balance ?? 0)}
        </p>
      </div>

      {/* Account balance graph over time */}
      <BalanceOverTimeChart
        balanceHistoryPoints={balanceHistory?.points}
        onHover={handleHover}
      />

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

      <Separator className="my-6" />

      <TransactionsTable transactions={transactions} />
    </div>
  );
}
