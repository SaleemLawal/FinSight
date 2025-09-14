import dayjs from 'dayjs';
import type{ Transaction } from 'types';
import calendar from "dayjs/plugin/calendar";
import { formatCurrency, groupByDate } from '~/lib/utils';

export default function TransactionsTable({ transactions }: { transactions: Transaction[] }) {
  const groupedTransactions = groupByDate(transactions);

  const sortedDates = Object.keys(groupedTransactions).sort((a, b) => dayjs(b).diff(dayjs(a)) );
  dayjs.extend(calendar);
  return (
    <div className="space-y-3">
      {sortedDates.map((date) => (
        <div key={date}>
          {/* Date header */}
          <h2 className="text-gray-400 text-sm font-semibold">
            {dayjs(date).calendar(null, {
              sameDay: "[Today]",
              lastDay: "[Yesterday]",
              lastWeek: "ddd, MMM D",
              sameElse: "ddd, MMM D",
            })}
          </h2>

          {/* Transactions for this date */}
          <div>
            {groupedTransactions[date].map((tx) => (
              <div key={tx.id} className='flex items-center py-2'>
                <div className='w-1/2 flex items-center gap-4'>
                <div className='font-medium'>{tx.merchant}</div>
                <div className='text-sm text-gray-400'>{tx.accountName}</div>
                </div>
                
                <div className="">{tx.categoryId ?? 'Uncategorized'}</div>
                <div className='ml-auto'>{formatCurrency(tx.amount)}</div>
              </div>
            ))}
          </div>
        </div>
      ))}
    </div>
  );
}
