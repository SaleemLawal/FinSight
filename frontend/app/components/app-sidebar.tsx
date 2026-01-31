import {
  Flag,
  Layers,
  LayoutDashboard,
  Settings,
  TrendingUp,
  CreditCard,
  LineChart,
  PieChart,
  RefreshCw,
  Sparkles,
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
  SidebarFooter,
} from '@/components/ui/sidebar';

const items = [
  {
    title: 'Dashboard',
    url: '/',
    icon: LayoutDashboard,
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
    icon: TrendingUp,
  },
  {
    title: 'Accounts',
    url: '/accounts',
    icon: CreditCard,
  },
  {
    title: 'Investments',
    url: '/investments',
    icon: LineChart,
  },
  {
    title: 'Categories',
    url: '/categories',
    icon: PieChart,
  },
  {
    title: 'Recurrings',
    url: '/recurrings',
    icon: RefreshCw,
  },
];

export function AppSidebar() {
  const location = useLocation();
  return (
    <Sidebar className="border-r border-sidebar-border/50">
      <SidebarContent className="px-2">
        {/* Logo Section */}
        <SidebarGroup className="pt-6 pb-2">
          <SidebarGroupLabel className="flex items-center gap-3 px-3 mb-6">
            <div className="relative">
              <div className="w-9 h-9 rounded-xl bg-primary flex items-center justify-center">
                <Sparkles className="w-5 h-5 text-primary-foreground" />
              </div>
              <div className="absolute -bottom-0.5 -right-0.5 w-2.5 h-2.5 rounded-full bg-positive border-2 border-sidebar" />
            </div>
            <div className="flex flex-col">
              <span className="font-display text-lg text-sidebar-foreground tracking-tight">
                FinSight
              </span>
              <span className="text-[10px] font-mono text-muted-foreground uppercase tracking-widest">
                Command Center
              </span>
            </div>
          </SidebarGroupLabel>
          
          {/* Navigation */}
          <SidebarGroupContent>
            <SidebarMenu className="space-y-1">
              {items.map((item, index) => {
                const isActive = location.pathname === item.url;
                return (
                  <SidebarMenuItem 
                    key={item.title}
                    className="animate-fade-in-up"
                    style={{ animationDelay: `${index * 0.05}s` }}
                  >
                    <SidebarMenuButton
                      asChild
                      className={`
                        group relative px-3 py-2.5 rounded-xl transition-all duration-300
                        ${isActive
                          ? 'bg-gradient-to-r from-primary/20 to-primary/10 text-primary shadow-sm'
                          : 'text-muted-foreground hover:text-sidebar-foreground hover:bg-sidebar-accent/50'
                        }
                      `}
                    >
                      <Link to={item.url} className="flex items-center gap-3">
                        {isActive && (
                          <div className="absolute left-0 top-1/2 -translate-y-1/2 w-1 h-6 rounded-r-full bg-primary" />
                        )}
                        <item.icon className={`w-[18px] h-[18px] transition-transform duration-300 ${isActive ? 'text-primary' : 'group-hover:scale-110'}`} />
                        <span className="font-medium text-sm">{item.title}</span>
                        {isActive && (
                          <div className="ml-auto w-1.5 h-1.5 rounded-full bg-primary animate-pulse" />
                        )}
                      </Link>
                    </SidebarMenuButton>
                  </SidebarMenuItem>
                );
              })}
            </SidebarMenu>
          </SidebarGroupContent>
        </SidebarGroup>
      </SidebarContent>
      
      {/* Footer */}
      <SidebarFooter className="p-4 border-t border-sidebar-border/30">
        <SidebarMenuButton
          asChild
          className="px-3 py-2.5 rounded-xl text-muted-foreground hover:text-sidebar-foreground hover:bg-sidebar-accent/50 transition-all duration-300"
        >
          <Link to="/settings" className="flex items-center gap-3">
            <Settings className="w-[18px] h-[18px]" />
            <span className="font-medium text-sm">Settings</span>
          </Link>
        </SidebarMenuButton>
      </SidebarFooter>
    </Sidebar>
  );
}
