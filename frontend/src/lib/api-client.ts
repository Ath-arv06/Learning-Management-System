import axios from 'axios';

function resolveBaseUrl(): string {
  const raw = (import.meta.env.VITE_API_URL || '').trim();
  if (!raw) return '/api/v1';
  const withProtocol = /^https?:\/\//i.test(raw) ? raw : `https://${raw}`;
  const trimmed = withProtocol.replace(/\/+$/, '');
  return trimmed.endsWith('/api/v1') ? trimmed : `${trimmed}/api/v1`;
}

export const apiClient = axios.create({
  baseURL: resolveBaseUrl(),
  withCredentials: true,
  headers: {
    'Content-Type': 'application/json',
  },
});

apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // Dispatched when session expires
      console.warn('Authentication required or session expired.');
    }
    return Promise.reject(error);
  }
);
