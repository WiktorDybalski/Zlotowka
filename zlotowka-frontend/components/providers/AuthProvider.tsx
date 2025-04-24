"use client";

import routes from "@/routes";
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
  setLogin: (token: string) => void;
  setLogout: () => void;
  isLogged: () => boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

interface AuthProviderProps {
  children: ReactNode;
}

const localStorageTokenField = "loginToken";

export function AuthProvider({ children }: AuthProviderProps) {
  const [token, setToken] = useState<string | null>(null);
  const [hasMounted, setHasMounted] = useState(false);

  useEffect(() => {
    if (typeof window !== "undefined") {
      const storedToken = localStorage.getItem(localStorageTokenField);
      setToken(storedToken);
      setHasMounted(true);
      console.log("Loaded token: " + storedToken); //TODO: remove
    }
  }, []);

  useEffect(() => {
    // TODO: remove this
    console.log("new topken " + token);
  }, [token]);

  const setLogin = (newToken: string) => {
    setToken(newToken);
    localStorage.setItem(localStorageTokenField, newToken);
  };

  const setLogout = () => {
    setToken(null);
    localStorage.removeItem(localStorageTokenField);
  };

  const isLogged = () => !!token;

  if (!hasMounted) return null;

  return (
    <AuthContext.Provider value={{ token, setLogin, setLogout, isLogged }}>
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
