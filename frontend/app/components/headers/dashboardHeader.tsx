import { Separator } from '../ui/separator';
import { SidebarTrigger } from '../ui/sidebar';

export default function DashboardHeader() {
  return (
    <>
      <div className="flex p-2 items-center gap-3">
        <SidebarTrigger />
        <Separator
          orientation="vertical"
          className="data-[orientation=vertical]:h-4 bg-sidebar-border"
        />
        <span className="text-md font-medium text-secondary-foreground">
          Dashboard
        </span>
      </div>
      <Separator />
    </>
  );
}
