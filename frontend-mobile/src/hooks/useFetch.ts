import { useEffect } from "react";
import { useApi } from "./useApi";

/**
 * 컴포넌트 마운트 시 자동으로 데이터를 페칭하는 훅.
 * useApi를 래핑하여 useEffect를 자동으로 수행함.
 *
 * @template T - 응답 데이터 타입
 * @param endpoint - API 엔드포인트 경로
 * @param options - 추가 옵션
 * @returns 데이터, 로딩 상태, 에러, 리페치 함수
 */
export function useFetch<T>(
  endpoint: string,
  options?: {
    skip?: boolean;
    onSuccess?: (data: T) => void;
    onError?: (error: Error) => void;
  }
) {
  const { data, loading, error, refetch } = useApi<T>(endpoint, options);

  useEffect(() => {
    if (!options?.skip) {
      refetch();
    }
  }, [endpoint, options?.skip, refetch]);

  return {
    data,
    loading,
    error,
    refetch,
  };
}
