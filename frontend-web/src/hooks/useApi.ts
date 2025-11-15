import { useState, useCallback } from "react";
import { apiClient } from "../../../shared/api-client";

/**
 * API 호출을 수행하고 로딩/에러 상태를 관리하는 훅.
 *
 * @template T - 응답 데이터 타입
 * @param endpoint - API 엔드포인트 경로
 * @param options - 추가 옵션
 * @returns 데이터, 로딩 상태, 에러, 리페치 함수
 */
export function useApi<T>(
  endpoint: string,
  options?: {
    skip?: boolean;
    onSuccess?: (data: T) => void;
    onError?: (error: Error) => void;
  }
) {
  const [data, setData] = useState<T | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  const fetchData = useCallback(async () => {
    try {
      setLoading(true);
      setError(null);
      const result = await apiClient.get<T>(endpoint);
      setData(result);
      options?.onSuccess?.(result);
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : "Unknown error";
      setError(errorMessage);
      options?.onError?.(new Error(errorMessage));
    } finally {
      setLoading(false);
    }
  }, [endpoint, options]);

  return {
    data,
    loading,
    error,
    refetch: fetchData,
  };
}
