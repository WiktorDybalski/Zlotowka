"use client";

import routes from "@/routes";
import { UserData } from "@/services/UserService";
import { redirect } from "next/navigation";
import React, {
  createContext,
  useContext,
  useState,
  ReactNode,
  useEffect,
} from "react";

interface AuthContextType {
  token: string | null;
  setLogin: (token: string, newUserId: number) => void;
  setLogout: () => void;
  isLogged: () => boolean;
  userId: number | null;
  setUserDataWithinSameToken: (newUserData: UserData) => void;
  userData: UserData | null;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

interface AuthProviderProps {
  children: ReactNode;
}

const localStorageTokenField = "loginToken";
const localStorageUserIdField = "userId";

export function AuthProvider({ children }: AuthProviderProps) {
  const [token, setToken] = useState<string | null>(null);
  const [hasMounted, setHasMounted] = useState(false);

  const [userId, setUserId] = useState<number | null>(null);
  const [userData, setUserData] = useState<UserData | null>(null);

  useEffect(() => {
    if (typeof window !== "undefined") {
      const storedToken = localStorage.getItem(localStorageTokenField);
      setToken(storedToken);
      const storedUserId = localStorage.getItem(localStorageUserIdField);
      if (storedUserId) {
        setUserId(parseInt(storedUserId, 10));
      } else {
        setUserId(null);
      }
      setHasMounted(true);
      // console.log("Loaded token: " + storedToken); //TODO: remove
    }
  }, []);

  // useEffect(() => {
  //   // TODO: remove this
  //   console.log("new topken " + token, "id=", userId);
  // }, [token]);

  const setLogin = (newToken: string, newUserId: number) => {
    setToken(newToken);
    setUserId(newUserId);
    localStorage.setItem(localStorageTokenField, newToken);
    localStorage.setItem(localStorageUserIdField, newUserId.toString());
  };

  const setLogout = () => {
    setToken(null);
    setUserId(null);
    localStorage.removeItem(localStorageTokenField);
    localStorage.removeItem(localStorageUserIdField);
  };

  function setUserDataWithinSameToken(newUserData: UserData) {
    setUserData(newUserData);
  }

  const isLogged = () => !!token;

  if (!hasMounted) return null;

  return (
    <AuthContext.Provider
      value={{
        token,
        setLogin,
        setLogout,
        isLogged,
        userId,
        setUserDataWithinSameToken,
        userData,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth(): AuthContextType {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error("useLogin has to be used within a LoginProvider");
  }
  return context;
}

interface WithAuthProps {
  children: ReactNode;
}

export function EnterWithAuth({ children }: WithAuthProps) {
  const { isLogged } = useAuth();

  if (!isLogged()) {
    console.log("User not logged in, auto redirect to login page...");
    redirect(routes.login.pathname);
    return null;
  }

  return <>{children}</>;
}

export function RedirectWhenLogged({ children }: WithAuthProps) {
  const { isLogged } = useAuth();

  if (isLogged()) {
    console.log("User already logged in, auto redirect to dashboard...");
    redirect(routes.dashboard.pathname);
    return null;
  }

  return <>{children}</>;
}
