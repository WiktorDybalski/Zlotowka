"use client";

import { API_HOST } from "@/lib/config";

export async function sendToBackendWithoutReturningJson(
  input: RequestInfo,
  init?: RequestInit,
  errorMessage: string = "Error fetching data",
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
  errorMessage: string = "Error fetching data",
) {
  input = API_HOST + "/" + input;
  try {
    const response = await fetch(input, init);
    if (!response.ok) {
      let errorMessageToThrow = errorMessage + " ";
      try {
        const errorResponse = await response.json();
        if (errorResponse && errorResponse.message) {
          errorMessageToThrow += errorResponse.message;
        } else if (errorResponse && errorResponse.error) {
          errorMessageToThrow += errorResponse.error;
        } else {
          errorMessageToThrow += response.statusText;
        }
      } catch {
        errorMessageToThrow += response.statusText;
      }
      throw new Error(errorMessageToThrow);
    }
    return await response.json();
  } catch (error) {
    console.log("Error fetching data:", error);
    if (error instanceof Error && error.message === "Failed to fetch") {
      throw new Error(errorMessage + " \n Backend doesn't work :(");
    }
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
