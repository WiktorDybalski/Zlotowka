"use client";

import React, {
  createContext,
  useContext,
  useState,
  ReactNode,
  useEffect,
} from "react";

interface AuthContextType {
  token: string | null;
  login: (token: string) => void;
  logout: () => void;
  isLogged: () => boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

interface LoginProviderProps {
  children: ReactNode;
}

const localStorageTokenField = "loginToken";

export function LoginProvider({ children }: LoginProviderProps) {
  const [token, setToken] = useState<string | null>(null);
  const [hasMounted, setHasMounted] = useState(false);

  useEffect(() => {
    if (typeof window !== "undefined") {
      const storedToken = localStorage.getItem(localStorageTokenField);
      setToken(storedToken);
      setHasMounted(true);
      console.log("Loaded token: " + storedToken);
    }
  }, []);

  useEffect(() => {
    // TODO: remove this
    console.log("new topken" + token);
  }, [token]);

  const login = (newToken: string) => {
    setToken(newToken);
    localStorage.setItem(localStorageTokenField, newToken);
    alert("Zalogowano pomyślnie!" + newToken); // TODO: remove this alert
  };

  const logout = () => {
    setToken(null);
    localStorage.removeItem(localStorageTokenField);
    alert("Wylogowano pomyślnie!"); // TODO: remove this alert
  };

  const isLogged = () => !!token;

  if (!hasMounted) return null;

  return (
    <AuthContext.Provider value={{ token, login, logout, isLogged }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useLogin(): AuthContextType {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error("useLogin has to be used within a LoginProvider");
  }
  return context;
}
