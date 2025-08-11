import { Button } from '~/components/ui/button';
import type { Route } from './+types/plaid';
import { usePlaidLink, type PlaidLinkOptions } from 'react-plaid-link';
import { useEffect, useState } from 'react';
import axios from 'axios';

export function meta({}: Route.MetaArgs) {
  return [{ title: 'FinSight' }, { name: 'description', content: 'FinSight' }];
}

export default function Plaid() {
  const [linkToken, setLinkToken] = useState<string | null>(null);
  const [isAuthenticated, setIsAuthenticated] = useState<boolean>(false);
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [authError, setAuthError] = useState<string | null>(null);
  const [items, setItems] = useState<
    { institutionName: string; itemId: string }[]
  >([]);
  const [mode, setMode] = useState<'create' | 'update'>('create');

  useEffect(() => {
    const loadItems = async () => {
      await checkAuthentication();
      const { data } = await axios.get(
        'http://localhost:8080/api/plaid/items',
        { withCredentials: true }
      );
      setItems(data);
    };
    loadItems();
  }, []);

  const checkAuthentication = async () => {
    try {
      setIsLoading(true);
      setAuthError(null);

      await axios.post(
        'http://localhost:8080/auth/login',
        {
          email: 'test@example.com',
          password: 'password',
        },
        {
          withCredentials: true,
        }
      );

      setIsAuthenticated(true);
      return true;
    } catch (error) {
      console.error('Authentication failed:', error);
      setAuthError('Authentication failed. Please login.');
      setIsAuthenticated(false);
      return false;
    } finally {
      setIsLoading(false);
    }
  };

  const createToken = async (mode: 'create' | 'update', itemId?: string) => {
    setMode(mode);
    const body = mode === 'create' ? { mode } : { mode, itemId };
    const { data } = await axios.post(
      'http://localhost:8080/api/plaid/create-token',
      body,
      { withCredentials: true }
    );
    setLinkToken(data.link_token);
  };

  const config: PlaidLinkOptions = {
    onSuccess: async (public_token, metadata) => {
      console.log('public_token', public_token);

      // Ensure user is still authenticated before exchanging token
      const authCheck = await checkAuthentication();
      if (!authCheck) {
        console.error('User not authenticated for token exchange');
        return;
      }

      try {
        const { data } = await axios.post(
          `http://localhost:8080/api/plaid/exchange-token`,
          {
            public_token,
            mode,
          },
          { withCredentials: true }
        );
        setLinkToken(null);
        // only add data item if the itemId is not already in the items array
        setItems((prevItems) =>
          prevItems.find((item) => item.itemId === data.itemId)
            ? prevItems
            : [
                ...prevItems,
                { itemId: data.itemId, institutionName: data.institutionName },
              ]
        );
        console.log('Token exchange successful');
      } catch (error) {
        console.error('Token exchange failed:', error);
      }
    },
    onExit: (err, metadata) => {
      console.log('Plaid Link exited:', err, metadata);
    },
    token: linkToken,
  };

  const { open, ready } = usePlaidLink(config);

  const handleNewAccount = async () => {
    const authSuccess = await checkAuthentication();
    if (authSuccess && !linkToken) {
      await createToken('create');
    }
  };

  const handleUpdateAccount = async (itemId: string) => {
    console.log('handleUpdateAccount', itemId);
    const authSuccess = await checkAuthentication();
    if (authSuccess && !linkToken) {
      await createToken('update', itemId);
    }
  };

  useEffect(() => {
    if (ready && linkToken) {
      open();
    }
  }, [ready, linkToken, open]);

  return (
    <div className="space-y-4">
      <div className="grid grid-cols-2 gap-4">
        <Button
          onClick={handleNewAccount}
          disabled={isLoading}
          variant={isAuthenticated ? 'outline' : 'default'}
        >
          {isLoading ? 'Checking...' : 'Add New Account'}
        </Button>
      </div>

      {authError && (
        <div className="text-red-500 text-sm mt-2">{authError}</div>
      )}

      {isAuthenticated && (
        <div className="text-green-500 text-sm">
          ✓ Ready to connect your bank account
        </div>
      )}

      {items.length > 0 && (
        <div className="mt-4">
          <h3 className="text-lg font-semibold mb-2">Connected Accounts:</h3>
          <ul className="space-y-2">
            {items.map((item, index) => (
              <li
                key={item.itemId}
                className="flex items-center justify-between p-2 bg-gray-50 rounded"
              >
                <span>{item.institutionName}</span>
                <Button
                  onClick={() => handleUpdateAccount(item.itemId)}
                  size="sm"
                  variant="outline"
                >
                  Update
                </Button>
              </li>
            ))}
          </ul>
        </div>
      )}
    </div>
  );
}
