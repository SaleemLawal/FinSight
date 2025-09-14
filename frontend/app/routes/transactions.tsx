import { Plus } from 'lucide-react';
import React, { useEffect, useState } from 'react'
import type { Transaction } from 'types';
import TransactionsTable from '~/components/TransactionsTable';
import { Button } from '~/components/ui/button';
import { fetchUserTransactions } from '~/lib/api/accounts';

export default function transactions() {
    const [transactions, setTransactions] = useState<Transaction[]>([]);

    useEffect(() => {
        const fetchTransactions = async () => {
            const transactions = await fetchUserTransactions();
            setTransactions(transactions);
        };
        fetchTransactions();
    }, []);
    console.log(transactions);

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
            <TransactionsTable transactions={transactions} />
            </div>

          </div>
    
          {/* <div className="flex flex-col shadow-md">
            <div className="flex items-center justify-between border-b px-4 py-2 h-12 shadow-sm">
              <h1 className="font-medium">{""}</h1>
              <button className="text-sm text-muted-foreground">
                Manage connection
              </button>
            </div>
          </div> */}
        </div>
      );
}
