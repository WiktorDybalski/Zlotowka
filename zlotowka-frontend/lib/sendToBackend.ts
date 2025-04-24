"use client";

import { API_HOST } from "@/lib/config";

export default async function sendToBackend(
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
    return await response.json();
  } catch (error) {
    console.log("Error fetching data:", error);
    throw error;
  }
}

export function getAuthHeader(token: string) {
  return {
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${token}`,
    },
  };
}
