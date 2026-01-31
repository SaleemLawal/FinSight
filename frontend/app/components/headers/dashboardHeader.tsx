import { Separator } from '../ui/separator';
import { SidebarTrigger } from '../ui/sidebar';

export default function DashboardHeader() {
  return (
    <header className="sticky top-0 z-40 bg-background/95 backdrop-blur-sm border-b border-border">
      <div className="flex px-6 py-3 items-center gap-4">
        <SidebarTrigger className="hover:bg-accent transition-colors rounded-lg" />
        <Separator
          orientation="vertical"
          className="data-[orientation=vertical]:h-5 bg-border"
        />
        <div className="flex items-baseline gap-3">
          <h1 className="text-lg font-semibold tracking-tight text-foreground">
            Dashboard
          </h1>
        </div>
        <div className="ml-auto flex items-center gap-3">
          <span className="text-xs font-mono text-muted-foreground">
            {new Date().toLocaleDateString('en-US', { 
              weekday: 'short', 
              month: 'short', 
              day: 'numeric' 
            })}
          </span>
          <div className="w-2 h-2 rounded-full bg-chart-5" />
        </div>
      </div>
    </header>
  );
}
