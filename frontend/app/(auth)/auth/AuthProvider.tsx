"use client";

import {createContext, useCallback, useContext, useEffect, useMemo, useState, 
  type ReactNode,
} from "react";

import {
  signIn as authenticate,
  type SignInRequest,
} from "../../../lib/auth-client";

type AuthContextValue = {
  isSignedIn: boolean;
  signIn: (request: SignInRequest) => Promise<void>;
  signOut: () => void;
};

const AuthContext = createContext<AuthContextValue | null>(null);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [token, setToken] = useState<string | null>(null);

  useEffect(() => {
    setToken(window.sessionStorage.getItem("authToken"));
  }, []);

  const signIn = useCallback(async (request: SignInRequest) => {
    const result = await authenticate(request);

    if (result.ok === false) {
      throw new Error(result.error.message);
    }

    const newToken = result.data.token;
    window.sessionStorage.setItem("authToken", newToken);
    setToken(newToken);
  }, []);

  const signOut = useCallback(() => {
    window.sessionStorage.removeItem("authToken");
    setToken(null);
  }, []);

  const value = useMemo(
    () => ({ isSignedIn: Boolean(token), signIn, signOut }),
    [token, signIn, signOut],
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const context = useContext(AuthContext);

  if (!context) {
    throw new Error("useAuth must be used within an AuthProvider.");
  }

  return context;
}
