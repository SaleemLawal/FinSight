import React, { useEffect, useState } from 'react';
import BalanceOverTimeChart from './balanceOverTimeChart';
import type { BalanceHistoryPoint, RangeKey } from 'types';
import { fetchAccountBalanceHistory } from '~/lib/api/accounts';

interface BalanceOverTimeChartWrapperProps {
  accountId: string;
  range: RangeKey;
  allowHover?: boolean;
  setPercentageChangeInBalance?: (
    accountId: string,
    percentage: number
  ) => void;
}

export default function BalanceOverTimeChartWrapper({
  accountId,
  range,
  allowHover = true,
  setPercentageChangeInBalance,
}: BalanceOverTimeChartWrapperProps) {
  const [balanceHistoryPoints, setBalanceHistoryPoints] = useState<
    BalanceHistoryPoint[] | undefined
  >(undefined);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);
        const balanceHistory = await fetchAccountBalanceHistory(
          accountId,
          range
        );
        setBalanceHistoryPoints(balanceHistory.points);

        // Calculate percentage and call callback immediately after setting data
        if (balanceHistory.points && balanceHistory.points.length > 1) {
          const percentageChangeInBalance =
            ((balanceHistory.points[balanceHistory.points.length - 1]
              ?.balance ?? 0) -
              (balanceHistory.points[0]?.balance ?? 0)) /
            (balanceHistory.points[0]?.balance ?? 0);

          if (setPercentageChangeInBalance) {
            setPercentageChangeInBalance(accountId, percentageChangeInBalance);
          }
        }
      } catch (error) {
        console.error('Failed to fetch balance history:', error);
        setBalanceHistoryPoints(undefined);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [accountId, range]);

  if (loading) {
    return <div className="h-[110px] animate-pulse bg-gray-200 rounded"></div>;
  }

  return (
    <BalanceOverTimeChart
      balanceHistoryPoints={balanceHistoryPoints}
      allowHover={allowHover}
    />
  );
}
