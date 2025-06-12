import { useAuth } from "@/components/providers/AuthProvider";
import sendToBackend, { getAuthHeader } from "@/lib/sendToBackend";
import { Currency } from "./CurrencyController";
import { useEffect } from "react";
import { applyDarkMode } from "@/utils/Darkmode";

export interface UserData {
  firstName: string;
  lastName: string;
  email: string;
  phoneNumber?: string;
  dateOfJoining: string; //ISO date
  currentBudget: number;
  currency: Currency;
  darkMode: boolean;
  notificationsByEmail: boolean;
  notificationsByPhone: boolean;
}

export function useUserService() {
  const { token, userData, setUserDataWithinSameToken } = useAuth();

  useEffect(() => {
    if (userData) applyDarkMode(userData.darkMode);
  }, [userData]);

  if (!token) throw new Error("User Logged Out (Token not provided)!");

  const withAuthHeader = getAuthHeader(token);

  async function getUserData(): Promise<UserData> {
    if (userData)
      return userData; // Return cached user data if available
    else {
      const userData = await fetchUserData();
      setUserDataWithinSameToken(userData); // Update user data in the context
      return userData; // Return fetched user data
    }
  }

  async function fetchUserData(): Promise<UserData> {
    const data = await sendToBackend(
      `user/account`,
      withAuthHeader,
      "Nie udało się pobrać danych użytkownika",
    );
    applyDarkMode(data.darkMode);
    return data;
  }

  return {
    getUserData,
    fetchUserData,
  };
}
