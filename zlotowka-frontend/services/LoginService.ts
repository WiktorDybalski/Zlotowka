"use client";

import { useAuth } from "@/components/providers/AuthProvider";
import sendToBackend from "@/lib/sendToBackend";

async function getMockedResponse() {
  //TODO delete!!!!!
  await new Promise((resolve) => setTimeout(resolve, 2000)); // Simulate network delay
  return {
    token: "mocked_token",
  };
}

export function useLoginService() {
  const Auth = useAuth();

  // Register (endpoint: POST /auth/register)
  async function registerUser(userData: {
    firstName: string;
    lastName: string;
    email: string;
    password: string;
  }) {
    const res = await sendToBackend(
      `auth/register`,
      {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(userData),
      },
      "Failed to register user"
    );
    // const res = await getMockedResponse(); // TODO: delete Mocked response for testing
    if (res.token) {
      Auth.setLogin(res.token); //  Set the token in the auth context
    } else {
      throw new Error("Token not found in response");
    }
    return res;
  }

  // Login (endpoint: POST /auth/login)
  async function loginUser(credentials: { email: string; password: string }) {
    const res = await sendToBackend(
      `auth/login`,
      {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(credentials),
      },
      "Failed to login"
    );
    if (res.token) {
      Auth.setLogin(res.token); // Set the token in the auth context
    } else {
      throw new Error("Token not found in response");
    }
  }

  return {
    registerUser,
    loginUser,
  };
}
