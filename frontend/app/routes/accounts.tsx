import { Separator } from '~/components/ui/separator';
import type { Route } from './+types/accounts';


export function meta({}: Route.MetaArgs) {
  return [{ title: 'FinSight' }, { name: 'description', content: 'FinSight' }];
}

export default function accounts() {
  return (
    <div>
        <div className='grid grid-cols-2 gap-4'>
            <div>
                DIV 1
            </div>
            <Separator />
            <div>
                DIV 2
            </div>
        </div>
    </div>
  )
}
