import type { Route } from './+types/accounts';
import { Button } from '~/components/ui/button';
import { Plus } from 'lucide-react';
import AssetsVsDebt from '~/components/AssetsVsDebt';
import { useState, useEffect, useCallback } from 'react';
import {
  Accordion,
  AccordionContent,
  AccordionItem,
  AccordionTrigger,
} from '~/components/ui/accordion';
import { Avatar, AvatarFallback, AvatarImage } from '~/components/ui/avatar';
import { Badge } from '~/components/ui/badge';
import {
  fetchAccountBalanceHistory,
  fetchAccounts,
  fetchTransactionsById,
} from '~/lib/api/accounts';
import type {
  Account,
  BalanceHistory,
  BalanceHistoryPoint,
  RangeKey,
  Transaction,
} from 'types';
import { formatCurrency } from '~/lib/utils';
import AccountDetail from '~/components/accountDetail';
import BalanceOverTimeChartWrapper from '~/components/charts/balanceOverTimeChartWrapper';

export function meta({}: Route.MetaArgs) {
  return [{ title: 'FinSight' }, { name: 'description', content: 'FinSight' }];
}

export default function Acounts() {
  const [range, setRange] = useState<RangeKey>('1W');
  const [selectedAccountType, setSelectedAccountType] = useState<string | null>(
    'Credit Card'
  );
  const [accounts, setAccounts] = useState<Account[]>([]);
  const [selectedAccount, setSelectedAccount] = useState<Account | null>(null);
  const [transactions, setTransactions] = useState<Transaction[]>([]);
  const [percentageChanges, setPercentageChanges] = useState<
    Record<string, number>
  >({});

  useEffect(() => {
    const fetchAccountsData = async () => {
      const data: Account[] = await fetchAccounts();
      setAccounts(data);
      setSelectedAccount(data[0] ?? null);
    };
    fetchAccountsData();
  }, []);

  useEffect(() => {
    const fetchTransactionsData = async () => {
      const data: Transaction[] = await fetchTransactionsById(
        selectedAccount?.id
      );
      setTransactions(data);
    };
    fetchTransactionsData();
  }, [selectedAccount]);

  const handleAccountClick = (accountType: string, account: Account) => {
    setSelectedAccountType(accountType);
    setSelectedAccount(account);
  };

  const handlePercentageChange = useCallback(
    (accountId: string, percentage: number) => {
      setPercentageChanges((prev) => ({
        ...prev,
        [accountId]: percentage,
      }));
    },
    []
  );

  return (
    <div className="grid grid-cols-1 md:grid-cols-2 border rounded-md overflow-hidden h-full">
      <div className="flex flex-col border-r">
        <div className="flex items-center gap-2 border-b px-4 py-2 h-12 shadow-sm">
          <h1 className="font-medium">Accounts</h1>
          <Button variant="outline" size="sm" className="px-1.5!">
            <Plus className="size-4" />
          </Button>
        </div>
        <div className="flex-1 overflow-auto p-8">
          <AssetsVsDebt nav={false} range={range} setRange={setRange} />

          <Accordion
            type="multiple"
            defaultValue={[
              'credit-cards',
              'depository',
              'investments',
              'loans',
            ]}
            className="mt-8"
          >
            <AccordionItem value="credit-cards">
              <AccordionTrigger className="font-bold text-md">
                Credit Cards
              </AccordionTrigger>
              <AccordionContent className="flex flex-col gap-4">
                {accounts
                  .filter((account) => account.type === 'CREDIT')
                  .map((account) => (
                    <div
                      onClick={() => handleAccountClick('Credit Card', account)}
                      className="cursor-pointer"
                      key={account.id}
                    >
                      <div className="grid grid-cols-[auto_1fr_auto] md:grid-cols-[0.15fr_1fr_auto_auto] lg:grid-cols-[0.1fr_1fr_0.4fr_0.3fr_0.3fr] items-center gap-3">
                        <Avatar className="size-8">
                          <AvatarImage src="https://github.com/shadcn.png" />
                          <AvatarFallback>CN</AvatarFallback>
                        </Avatar>
                        <div>
                          <p>
                            {account.name}{' '}
                            <span className="text-muted-foreground">
                              {account.last_four}
                            </span>
                          </p>
                          <p className="text-muted-foreground text-xs">
                            1 day ago
                          </p>
                        </div>
                        <div className="hidden lg:block  ">
                          <BalanceOverTimeChartWrapper
                            accountId={account.id}
                            range={range}
                            allowHover={false}
                            setPercentageChangeInBalance={
                              handlePercentageChange
                            }
                          />
                        </div>
                        <Badge className="hidden md:inline-flex bg-green-200 text-green-400 items-center gap-1 py-0.5 px-2 rounded-full text-sm font-semibold mt-1">
                          ↑{' '}
                          {(percentageChanges[account.id] * 100)?.toFixed(2) ??
                            '0.00'}
                          %
                        </Badge>
                        <div className="ml-auto">
                          <p>${account.balance}</p>
                        </div>
                      </div>
                    </div>
                  ))}
              </AccordionContent>
            </AccordionItem>

            <AccordionItem value="depository">
              <AccordionTrigger className="font-bold text-md">
                Depository
              </AccordionTrigger>
              <AccordionContent className="flex flex-col gap-4">
                {accounts
                  .filter((account) => account.type === 'DEPOSITORY')
                  .map((account) => (
                    <div
                      onClick={() => handleAccountClick('Checking', account)}
                      className="cursor-pointer"
                      key={account.id}
                    >
                      <div className="grid grid-cols-[auto_1fr_auto] md:grid-cols-[0.15fr_1fr_auto_auto] lg:grid-cols-[0.1fr_1fr_0.4fr_0.3fr_0.3fr] items-center gap-3">
                        <Avatar className="size-8">
                          <AvatarImage src="https://github.com/shadcn.png" />
                          <AvatarFallback>CN</AvatarFallback>
                        </Avatar>
                        <div>
                          <p>
                            {account.name}{' '}
                            <span className="text-muted-foreground">
                              {account.last_four}
                            </span>
                          </p>
                          <p className="text-muted-foreground text-xs">
                            1 day ago
                          </p>
                        </div>
                        <div className="hidden lg:block">
                          <BalanceOverTimeChartWrapper
                            accountId={account.id}
                            range={range}
                            allowHover={false}
                            setPercentageChangeInBalance={
                              handlePercentageChange
                            }
                          />
                        </div>
                        <Badge className="hidden md:inline-flex bg-green-200 text-green-400 items-center gap-1 py-0.5 px-2 rounded-full text-sm font-semibold mt-1">
                          ↑{' '}
                          {(percentageChanges[account.id] * 100)?.toFixed(2) ??
                            '0.00'}
                          %
                        </Badge>
                        <div className="ml-auto">
                          <p>{formatCurrency(account.balance)}</p>
                        </div>
                      </div>
                    </div>
                  ))}
              </AccordionContent>
            </AccordionItem>

            <AccordionItem value="investments">
              <AccordionTrigger className="font-bold text-md">
                Investments
              </AccordionTrigger>
              <AccordionContent className="flex flex-col gap-4">
                {accounts
                  .filter((account) => account.type === 'INVESTMENT')
                  .map((account) => (
                    <div
                      onClick={() => handleAccountClick('Investment', account)}
                      className="cursor-pointer"
                      key={account.id}
                    >
                      <div className="grid grid-cols-[auto_1fr_auto] md:grid-cols-[0.15fr_1fr_auto_auto] lg:grid-cols-[0.1fr_1fr_0.4fr_0.3fr_0.3fr] items-center gap-3">
                        <Avatar className="size-8">
                          <AvatarImage src="https://github.com/shadcn.png" />
                          <AvatarFallback>CN</AvatarFallback>
                        </Avatar>
                        <div>
                          <p>
                            {account.name}{' '}
                            <span className="text-muted-foreground">
                              {account.last_four}
                            </span>
                          </p>
                          <p className="text-muted-foreground text-xs">
                            1 day ago
                          </p>
                        </div>
                        <div className="hidden lg:block">
                          <BalanceOverTimeChartWrapper
                            accountId={account.id}
                            range={range}
                            allowHover={false}
                            setPercentageChangeInBalance={
                              handlePercentageChange
                            }
                          />
                        </div>
                        <Badge className="hidden md:inline-flex bg-green-200 text-green-400 items-center gap-1 py-0.5 px-2 rounded-full text-sm font-semibold mt-1">
                          ↑{' '}
                          {(percentageChanges[account.id] * 100)?.toFixed(2) ??
                            '0.00'}
                          %
                        </Badge>
                        <div className="ml-auto">
                          <p>{formatCurrency(account.balance)}</p>
                        </div>
                      </div>
                    </div>
                  ))}
              </AccordionContent>
            </AccordionItem>

            <AccordionItem value="loans">
              <AccordionTrigger className="font-bold text-md">
                Loans
              </AccordionTrigger>
              <AccordionContent className="flex flex-col gap-4">
                {accounts
                  .filter((account) => account.type === 'LOAN')
                  .map((account) => (
                    <div
                      onClick={() => handleAccountClick('Loan', account)}
                      className="cursor-pointer"
                      key={account.id}
                    >
                      <div className="grid grid-cols-[auto_1fr_auto] md:grid-cols-[0.15fr_1fr_auto_auto] lg:grid-cols-[0.1fr_1fr_0.4fr_0.3fr_0.3fr] items-center gap-3">
                        <Avatar className="size-8">
                          <AvatarImage src="https://github.com/shadcn.png" />
                          <AvatarFallback>CN</AvatarFallback>
                        </Avatar>
                        <div>
                          <p>
                            {account.name}{' '}
                            <span className="text-muted-foreground">
                              {account.last_four}
                            </span>
                          </p>
                          <p className="text-muted-foreground text-xs">
                            1 day ago
                          </p>
                        </div>
                        <div className="hidden lg:block">
                          <BalanceOverTimeChartWrapper
                            accountId={account.id}
                            range={range}
                            allowHover={false}
                            setPercentageChangeInBalance={
                              handlePercentageChange
                            }
                          />
                        </div>
                        <Badge className="hidden md:inline-flex bg-green-200 text-green-400 items-center gap-1 py-0.5 px-2 rounded-full text-sm font-semibold mt-1">
                          ↑{' '}
                          {(percentageChanges[account.id] * 100)?.toFixed(2) ??
                            '0.00'}
                          %
                        </Badge>
                        <div className="ml-auto">
                          <p>{formatCurrency(account.balance)}</p>
                        </div>
                      </div>
                    </div>
                  ))}
              </AccordionContent>
            </AccordionItem>
          </Accordion>
        </div>
      </div>

      <div className="flex flex-col shadow-md">
        <div className="flex items-center justify-between border-b px-4 py-2 h-12 shadow-sm">
          <h1 className="font-medium">{selectedAccountType}</h1>
          <button className="text-sm text-muted-foreground">
            Manage connection
          </button>
        </div>
        <div className="flex-1 overflow-auto p-8">
          {selectedAccount && (
            <AccountDetail
              account={selectedAccount}
              transactions={transactions}
            />
          )}
        </div>
      </div>
    </div>
  );
}
