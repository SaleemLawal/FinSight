import {
  Flag,
  Layers,
  Send,
  Settings,
  ChartNoAxesCombined,
  CreditCard,
  ChartNoAxesColumnIncreasing,
  ChartPie,
  Repeat,
  BadgeDollarSign,
} from 'lucide-react';
import { Link, useLocation } from 'react-router';

import {
  Sidebar,
  SidebarContent,
  SidebarGroup,
  SidebarGroupContent,
  SidebarGroupLabel,
  SidebarMenu,
  SidebarMenuButton,
  SidebarMenuItem,
} from '@/components/ui/sidebar';

const items = [
  {
    title: 'Dashboard',
    url: '/',
    icon: Send,
  },
  {
    title: 'Transactions',
    url: '/transactions',
    icon: Layers,
  },
  {
    title: 'Goals',
    url: '/goals',
    icon: Flag,
  },
  {
    title: 'Cash flow',
    url: '/cash-flow',
    icon: ChartNoAxesCombined,
  },
  {
    title: 'Accounts',
    url: '/accounts',
    icon: CreditCard,
  },
  {
    title: 'Investments',
    url: '/investments',
    icon: ChartNoAxesColumnIncreasing,
  },
  {
    title: 'Categories',
    url: '/categories',
    icon: ChartPie,
  },
  {
    title: 'Recurrings',
    url: '/recurrings',
    icon: Repeat,
  },
  {
    title: 'Settings',
    url: '/settings',
    icon: Settings,
  },
];

export function AppSidebar() {
  const location = useLocation();
  return (
    <Sidebar>
      <SidebarContent>
        <SidebarGroup>
          <SidebarGroupLabel className="font-semibold text-md text-[#000000] flex items-center gap-2">
            <BadgeDollarSign />
            <span>FinSight</span>
          </SidebarGroupLabel>
          <SidebarGroupContent>
            <SidebarMenu>
              {items.map((item) => {
                const isActive = location.pathname === item.url;
                return (
                  <SidebarMenuItem key={item.title}>
                    <SidebarMenuButton
                      asChild
                      className={
                        isActive
                          ? 'bg-primary text-primary-foreground hover:bg-primary hover:text-primary-foreground'
                          : ''
                      }
                    >
                      <Link to={item.url}>
                        <item.icon />
                        <span>{item.title}</span>
                      </Link>
                    </SidebarMenuButton>
                  </SidebarMenuItem>
                );
              })}
            </SidebarMenu>
          </SidebarGroupContent>
        </SidebarGroup>
      </SidebarContent>
    </Sidebar>
  );
}
