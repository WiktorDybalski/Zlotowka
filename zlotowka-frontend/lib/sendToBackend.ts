"use client";

import { API_HOST } from "@/lib/config";

export async function sendToBackendWithoutReturningJson(
  input: RequestInfo,
  init?: RequestInit,
  errorMessage: string = "Error fetching data"
) {
  input = API_HOST + "/" + input;
  try {
    const response = await fetch(input, init);
    if (!response.ok) {
      throw new Error(errorMessage + " " + response.statusText);
    }
    return response;
  } catch (error) {
    console.log("Error fetching data:", error);
    throw error;
  }
}

export default async function sendToBackend(
  input: RequestInfo,
  init?: RequestInit,
  errorMessage: string = "Error fetching data"
) {
  const responce = await sendToBackendWithoutReturningJson(
    input,
    init,
    errorMessage
  );
  return await responce.json();
}

export function getAuthHeader(token: string) {
  return {
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${token}`,
    },
  };
}
