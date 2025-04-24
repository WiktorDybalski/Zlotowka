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
      console.log("Loaded token: " + storedToken);
    }
  }, []);

  useEffect(() => {
    // TODO: remove this
    console.log("new topken" + token);
  }, [token]);

  const setLogin = (newToken: string) => {
    setToken(newToken);
    localStorage.setItem(localStorageTokenField, newToken);
    alert("Zalogowano pomyślnie!" + newToken); // TODO: remove this alert
  };

  const setLogout = () => {
    setToken(null);
    localStorage.removeItem(localStorageTokenField);
    alert("Wylogowano pomyślnie!"); // TODO: remove this alert
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
