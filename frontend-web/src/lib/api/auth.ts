import { apiClient, ApiResponse } from './client';

export interface TokenResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresIn: number;
}

export interface UserInfo {
  userId: number;
  email: string;
  displayId: string;
  nickname: string;
  profileImageUrl?: string;
}

export const authApi = {
  async refreshToken(refreshToken: string): Promise<ApiResponse<TokenResponse>> {
    return apiClient.post<TokenResponse>('/api/v1/auth/refresh', { refreshToken });
  },

  async getCurrentUser(): Promise<ApiResponse<UserInfo>> {
    return apiClient.get<UserInfo>('/api/v1/auth/me');
  },

  logout() {
    apiClient.clearToken();
  },

  setTokens(accessToken: string, refreshToken: string) {
    apiClient.setToken(accessToken);
    if (typeof window !== 'undefined') {
      localStorage.setItem('refreshToken', refreshToken);
    }
  },

  getAccessToken(): string | null {
    if (typeof window !== 'undefined') {
      return localStorage.getItem('accessToken');
    }
    return null;
  },

  getRefreshToken(): string | null {
    if (typeof window !== 'undefined') {
      return localStorage.getItem('refreshToken');
    }
    return null;
  },
};
