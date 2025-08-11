import axios, {
  type AxiosError,
  type AxiosInstance,
  type AxiosRequestConfig,
  type AxiosResponse,
} from 'axios';
import { API_URL } from '~/lib/constants';

// let inMemoryAuthToken: string | null = null;

// export function setAuthToken(token: string | null): void {
//   inMemoryAuthToken = token;
//   if (token) {
//     try {
//       localStorage.setItem('auth_token', token);
//     } catch {}
//   } else {
//     try {
//       localStorage.removeItem('auth_token');
//     } catch {}
//   }
// }

// function readAuthToken(): string | null {
//   if (inMemoryAuthToken) return inMemoryAuthToken;
//   try {
//     return localStorage.getItem('auth_token');
//   } catch {
//     return null;
//   }
// }

function createClient(baseURL: string): AxiosInstance {
  const instance = axios.create({
    baseURL,
    withCredentials: true,
    timeout: 15000,
    headers: {
      'Content-Type': 'application/json',
      Accept: 'application/json',
    },
  });

  instance.interceptors.request.use((config) => {
    // const token = readAuthToken();
    // if (token) {
    //   config.headers = config.headers ?? {};
    //   config.headers.Authorization = `Bearer ${token}`;
    // }
    return config;
  });

  instance.interceptors.response.use(
    (response: AxiosResponse) => response,
    (error: AxiosError) => {
      return Promise.reject(normalizeAxiosError(error));
    }
  );

  return instance;
}

// export const rootClient = createClient(ROOT_URL);
export const apiClient = createClient(API_URL);

export type ApiError = {
  statusCode: number | null;
  message: string;
  details?: unknown;
  raw?: unknown;
};

export function normalizeAxiosError(error: AxiosError): ApiError {
  const statusCode = error.response?.status ?? null;
  const messageFromServer =
    (error.response?.data as any)?.message ||
    (error.response?.data as any)?.error;

  return {
    statusCode,
    message: messageFromServer || error.message || 'Unexpected network error',
    details: error.response?.data,
    raw: error.toJSON ? error.toJSON() : error,
  };
}

export async function get<T>(
  url: string,
  config?: AxiosRequestConfig,
  client: AxiosInstance = apiClient
): Promise<T> {
  const { data } = await client.get<T>(url, config);
  return data;
}

export async function post<T, B = unknown>(
  url: string,
  body?: B,
  config?: AxiosRequestConfig,
  client: AxiosInstance = apiClient
): Promise<T> {
  const { data } = await client.post<T>(url, body, config);
  return data;
}

export async function put<T, B = unknown>(
  url: string,
  body?: B,
  config?: AxiosRequestConfig,
  client: AxiosInstance = apiClient
): Promise<T> {
  const { data } = await client.put<T>(url, body, config);
  return data;
}

export async function patch<T, B = unknown>(
  url: string,
  body?: B,
  config?: AxiosRequestConfig,
  client: AxiosInstance = apiClient
): Promise<T> {
  const { data } = await client.patch<T>(url, body, config);
  return data;
}

export async function del<T>(
  url: string,
  config?: AxiosRequestConfig,
  client: AxiosInstance = apiClient
): Promise<T> {
  const { data } = await client.delete<T>(url, config);
  return data;
}
