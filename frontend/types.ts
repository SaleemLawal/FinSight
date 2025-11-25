export type Account = {
  id: string;
  name: string;
  balance: number;
  createdAt: string;
  institution_name: string;
  institution_id: string;
  last_four: string;
  type: AccountType;
  userId: string;
};

export type AccountType = "DEPOSITORY" | "CREDIT" | "INVESTMENT" | "LOAN";

export type Transaction = {
  id: string;
  accountId: string;
  date: string;
  merchant: string;
  description: string;
  amount: number;
  isRecurring: boolean;
  isReviewed: boolean;
  categoryId: string;
  userId: string;
  createdAt: string;
  updatedAt: string;
};

export type BalanceHistory = {
  accountId: string;
  range: RangeKey;
  granularity: string;
  currency: string;
  points: BalanceHistoryPoint[];
};

export type BalanceHistoryPoint = {
  date: string;
  balance: number;
};

export type RangeKey = "1W" | "1M" | "3M" | "YTD" | "1Y" | "ALL";
