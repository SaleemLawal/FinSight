import { clsx, type ClassValue } from 'clsx';
import { twMerge } from 'tailwind-merge';
import dayjs from "dayjs";
import type { Transaction } from 'types';


function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs));
}

const hex2rgb = (hex: string) => {
  const res = /^#?([a-f\d]{2})([a-f\d]{2})([a-f\d]{2})$/i.exec(hex)!;
  return {
    r: parseInt(res[1], 16),
    g: parseInt(res[2], 16),
    b: parseInt(res[3], 16),
  };
};
const rgb2hex = (r: number, g: number, b: number) =>
  `#${[r, g, b]
    .map((x) => x.toString(16).padStart(2, '0'))
    .join('')}`.toUpperCase();

const blend = (c1: string, c2: string, t: number) => {
  const a = hex2rgb(c1);
  const b = hex2rgb(c2);
  return rgb2hex(
    Math.round(a.r + (b.r - a.r) * t),
    Math.round(a.g + (b.g - a.g) * t),
    Math.round(a.b + (b.b - a.b) * t)
  );
};

const ratioTocolor = (ratio: number) => {
  if (ratio <= 0.8) return '#00a63e'; // bright green
  if (ratio >= 1.1) return '#e7000b'; // strong red
  if (ratio <= 1.0) {
    // 0.8 → 1.0  (green → yellow)
    const t = (ratio - 0.8) / 0.2;
    return blend('#00a63e', '#ffdf20', t);
  }
  // 1.0 → 1.1  (yellow → red)
  const t = (ratio - 1.0) / 0.1;
  return blend('#ffdf20', '#e7000b', t);
};

const formatCurrency = (amount: number) => {
  return new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: 'USD',
    maximumFractionDigits: 2,
  }).format(Number(amount));
};

const switchBadgeColor = (value: number) => {
  if (value > 0) return 'bg-green-200 text-green-400';
  if (value < 0) return 'bg-red-200 text-red-400';
  return 'bg-gray-200 text-gray-400';
};

const groupByDate = (transactions: Transaction[]) => {
  return transactions.reduce<Record<string, Transaction[]>>((groups, tx) => {
    const date: string = dayjs(tx.date).format('YYYY-MM-DD');
    if (!groups[date]) {
      groups[date] = [];
    }
    groups[date].push(tx);
    return groups;
  }, {});
};

export { cn, ratioTocolor, formatCurrency, switchBadgeColor, groupByDate };
