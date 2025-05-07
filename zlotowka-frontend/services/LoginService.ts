"use client";

import { useAuth } from "@/components/providers/AuthProvider";
import sendToBackend from "@/lib/sendToBackend";

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
    if (res.token && res.user.userId) {
      Auth.setLogin(res.token, res.user.userId); //  Set the token in the auth context
      Auth.setUserDataWithinSameToken(res.user);

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

    if (res.token && res.user.userId) {
      Auth.setLogin(res.token, res.user.userId); // Set the token in the auth context
      Auth.setUserDataWithinSameToken(res.user);
    } else {
      throw new Error("Token not found in response");
    }
  }

  return {
    registerUser,
    loginUser,
  };
}
