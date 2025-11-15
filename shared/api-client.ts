/**
 * API Client - Shared between Web and Mobile
 */

// Determine API base URL from several environment sources.
// - Next.js: process.env.NEXT_PUBLIC_API_URL
// - Vite: import.meta.env.VITE_API_URL
// - CRA-style: process.env.REACT_APP_API_URL
// Fallback to empty string for proxy (Next.js rewrites).
function getApiBaseUrl(): string {
  // Prefer well-known env names, fall back to empty string for proxy.
  try {
    // Next.js public env
    // @ts-ignore
    if (typeof process !== "undefined" && process.env?.NEXT_PUBLIC_API_URL) {
      // @ts-ignore
      return process.env.NEXT_PUBLIC_API_URL;
    }
    // CRA-style env
    // @ts-ignore
    if (typeof process !== "undefined" && process.env?.REACT_APP_API_URL) {
      // @ts-ignore
      return process.env.REACT_APP_API_URL;
    }
    // Generic VITE env exposed via process in some setups
    // @ts-ignore
    if (typeof process !== "undefined" && process.env?.VITE_API_URL) {
      // @ts-ignore
      return process.env.VITE_API_URL;
    }
  } catch (e) {
    // ignore and fall back
  }

  // Return empty string to use relative URLs for Next.js rewrites / Vite proxy
  return "";
}

const API_BASE_URL = getApiBaseUrl();

// Debug: expose chosen base URL when running in dev tools
if (typeof console !== "undefined") {
  console.debug("[api-client] API_BASE_URL=", API_BASE_URL);
}

export interface ApiResponse<T> {
  data: T;
  status: number;
  message?: string;
}

export const apiClient = {
  async get<T>(endpoint: string): Promise<T> {
    try {
      const url = `${API_BASE_URL}${endpoint}`;
      if (typeof console !== "undefined")
        console.debug(`[api-client] GET ${url}`);
      const response = await fetch(url);
      if (!response.ok) {
        throw new Error(`API error: ${response.status}`);
      }
      const text = await response.text();
      return text as unknown as T;
    } catch (error) {
      console.error(`GET ${endpoint} failed:`, error);
      throw error;
    }
  },

  async post<T>(endpoint: string, data: unknown): Promise<T> {
    try {
      const url = `${API_BASE_URL}${endpoint}`;
      if (typeof console !== "undefined")
        console.debug(`[api-client] POST ${url}`, data);
      const response = await fetch(url, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(data),
      });
      if (!response.ok) {
        throw new Error(`API error: ${response.status}`);
      }
      return (await response.json()) as T;
    } catch (error) {
      console.error(`POST ${endpoint} failed:`, error);
      throw error;
    }
  },

  async put<T>(endpoint: string, data: unknown): Promise<T> {
    try {
      const url = `${API_BASE_URL}${endpoint}`;
      if (typeof console !== "undefined")
        console.debug(`[api-client] PUT ${url}`, data);
      const response = await fetch(url, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(data),
      });
      if (!response.ok) {
        throw new Error(`API error: ${response.status}`);
      }
      return (await response.json()) as T;
    } catch (error) {
      console.error(`PUT ${endpoint} failed:`, error);
      throw error;
    }
  },

  async delete<T>(endpoint: string): Promise<T> {
    try {
      const url = `${API_BASE_URL}${endpoint}`;
      if (typeof console !== "undefined")
        console.debug(`[api-client] DELETE ${url}`);
      const response = await fetch(url, {
        method: "DELETE",
      });
      if (!response.ok) {
        throw new Error(`API error: ${response.status}`);
      }
      return (await response.json()) as T;
    } catch (error) {
      console.error(`DELETE ${endpoint} failed:`, error);
      throw error;
    }
  },
};
