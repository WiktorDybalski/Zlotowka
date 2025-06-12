"use client";

import {
  useQuery,
  UseQueryOptions,
  UseQueryResult,
} from "@tanstack/react-query";
import { useEffect } from "react";
import toast from "react-hot-toast";

export function useQueryWithToast<TData = unknown>(
  options: UseQueryOptions<TData>,
  errorMessage?: string,
): UseQueryResult<TData> {
  const queryResult = useQuery<TData>(options);

  useEffect(() => {
    if (queryResult.error) {
      toast.error(
        queryResult.error.message || errorMessage || "An error occurred",
      );
    }
  }, [queryResult.error]);

  return queryResult;
}
