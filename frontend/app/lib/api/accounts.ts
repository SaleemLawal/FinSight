import { get } from './connectivity';
import type { Account, Transaction } from 'types';
import type { RangeKey } from 'types';

export async function fetchAccounts(): Promise<Account[]> {
  return get(`/user/accounts`);
}

export async function fetchTransactionsById(
  accountId: string | undefined
): Promise<Transaction[]> {
  if (!accountId) return [];
  return get(`/user/transactions/${accountId}`);
}

export async function fetchAccountBalanceHistory(
  accountId: string | undefined,
  range: RangeKey
): Promise<any> {
  if (!accountId) return [];
  return get(`/user/accounts/${accountId}/balance-history?range=${range}`);
}

// export async function loginDemoUser() {
//   return post<void, { email: string; password: string }>(
//     `${apiClient.defaults.baseURL?.includes('/api') ? '/plaid/noop' : '/auth/login'}`,
//     { email: 'test@example.com', password: 'password' },
//     undefined,
//     apiClient
//   );
// }
