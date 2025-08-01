import { Button } from '~/components/ui/button';
import type { Route } from './+types/plaid';
import {
  usePlaidLink,
  type PlaidLinkOptions,
}
  from 'react-plaid-link';
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

  const checkAuthentication = async () => {
    try {
      setIsLoading(true);
      setAuthError(null);
      console.log('Checking authentication...');
      
      const response = await axios.post("http://localhost:8080/auth/login", {
        "email": "test@example.com",
        "password": "password"
    }, {
        withCredentials: true
      });
      
      console.log('Auth check response:', response.data);
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

  const getLinkToken = async () => {
    try {
      console.log('Getting link token...');
      const response = await axios.post("http://localhost:8080/api/plaid/create-token", {}, {
        withCredentials: true
      });
      const data = response.data;
      setLinkToken(data.link_token);
      console.log('Link token received:', data.link_token);
    } catch (error) {
      console.error('Failed to get link token:', error);
      setAuthError('Failed to initialize Plaid. Please try again.');
    }
  };

  // Auto-authenticate on component mount
  // useEffect(() => {
  //   const initializeAuth = async () => {
  //     const authSuccess = await checkAuthentication();
  //     if (authSuccess) {
  //       await getLinkToken();
  //     }
  //   };
    
  //   initializeAuth();
  // }, []);

  console.log('linkToken', linkToken);
  console.log('isAuthenticated', isAuthenticated);

  const config: PlaidLinkOptions = {
    onSuccess: async (public_token, metadata) => {
      console.log('public_token', public_token);
      // Ensure user is still authenticated before exchanging token
      const authCheck = await checkAuthentication();
      if (!authCheck) {
        console.error('User not authenticated for token exchange');
        return;
      }

      const isUpdateMode = !public_token;
      if (isUpdateMode) {
        console.log("✅ Update mode completed for item:", metadata);
        return; 
      }

      try {
        await axios.post(`http://localhost:8080/api/plaid/exchange-token`, {
          public_token,
        }, {withCredentials: true});
        console.log('Token exchange successful');
      } catch (error) {
        console.error('Token exchange failed:', error);
      }
    },
    onExit: (err, metadata) => { 
      console.log('Plaid Link exited:', err, metadata);
    },
    onEvent: (eventName, metadata) => { 
      console.log('Plaid Link event:', eventName, metadata);
    },
    token: linkToken,
  };

  const { open, exit, ready } = usePlaidLink(config);

  const handleLogin = async () => {
    const authSuccess = await checkAuthentication();
    // if (authSuccess && !linkToken) {
    //   await getLinkToken();
    // }
  };

  const handleConnectPlaid = async () => {
    // Double-check authentication right before opening Plaid
    if (!isAuthenticated) {
      console.log('User not authenticated, checking auth...');
      const authSuccess = await checkAuthentication();
      if (!authSuccess) {
        console.error('Cannot open Plaid: User not authenticated');
        return;
      }
    }

    if (!linkToken) {
      console.log('No link token, getting one...');
      await getLinkToken();
    }

    if (ready && linkToken) {
      open();
    } else {
      console.error('Plaid not ready or no link token available');
    }
  };

  return (
    <div className="space-y-4">
      <div className="grid grid-cols-2 gap-4">
        <Button 
          onClick={handleLogin}
          disabled={isLoading}
          variant={isAuthenticated ? "outline" : "default"}
        >
          {isLoading ? 'Checking...' : isAuthenticated ? '✓ Authenticated' : 'Login'}
        </Button>
        
        <Button 
          onClick={handleConnectPlaid}
          disabled={!isAuthenticated || !ready || !linkToken || isLoading}
        >
          {!isAuthenticated ? 'Login Required' : 
           !linkToken ? 'Getting Token...' : 
           !ready ? 'Plaid Loading...' : 
           'Connect Plaid'}
        </Button>
      </div>
      
      {authError && (
        <div className="text-red-500 text-sm mt-2">
          {authError}
        </div>
      )}
      
      {isAuthenticated && (
        <div className="text-green-500 text-sm">
          ✓ Ready to connect your bank account
        </div>
      )}
    </div>
  );
}
