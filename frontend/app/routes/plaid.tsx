import { Button } from '~/components/ui/button';
import type { Route } from './+types/home';
import {
  usePlaidLink,
  type PlaidLinkOptions,
  type PlaidLinkOnSuccess,
}
  from 'react-plaid-link';
import { useEffect, useState } from 'react';
import axios from 'axios';

export function meta({}: Route.MetaArgs) {
  return [{ title: 'FinSight' }, { name: 'description', content: 'FinSight' }];
}

export default function Plaid() {
  const [linkToken, setLinkToken] = useState<string | null>(null);
  useEffect(() => {
    const getLinkToken = async () => {
      const response = await axios.post("http://localhost:8080/api/plaid/create_link_token");
      const data = response.data;
      setLinkToken(data.link_token);
    }
    getLinkToken();
  }, [])
  console.log('linkToken', linkToken);

  const config: PlaidLinkOptions = {
    onSuccess: async (public_token, metadata) => {
      console.log('public_token', public_token);
      await axios.get(`http://localhost:8080/api/plaid/extract-access-token/${public_token}`);
    },
    onExit: (err, metadata) => { },
    onEvent: (eventName, metadata) => { },
    token: linkToken,
  };

  const { open, exit, ready } = usePlaidLink(config);

  const handleConnectPlaid = () => {
    open();
  }
  return (
    <div>
      <div className='grid grid-cols-2 gap-4'>
        <Button onClick={handleConnectPlaid}>Connect Plaid</Button>
      </div>
    </div>
  );
}
