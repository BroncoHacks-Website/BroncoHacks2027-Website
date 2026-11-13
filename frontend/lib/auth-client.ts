import { apiFetch } from "./api-client";

export type AuthUser = {
  id: string;
  name: string;
  email: string;
};

export type SignInRequest = {
  email: string;
  password: string;
};

export type SignUpRequest = {
  name: string;
  email: string;
  password: string;
};

export type AuthResponse = {
  user: AuthUser;
  token: string;
};

export type AuthErrorResponse = {
  message: string;
};

export type AuthResult =
  | {
      ok: true;
      data: AuthResponse;
    }
  | {
      ok: false;
      error: AuthErrorResponse;
    };

let authToken: string | null = null; 

export async function signIn(
  request: SignInRequest
): Promise<{ token: string }> {
  const data = await apiFetch<{ token: string }>("/auth/signin", {
    method: "POST",
    body: JSON.stringify({
      email: request.email,
      password: request.password,
    }),
  });

  if (!data || typeof data.token !== "string" || !data.token) { //
    throw new Error("Sign-in response did not include a token.");
  }

  if (typeof window !== "undefined") {
    window.sessionStorage.setItem("authToken", data.token); //store authentication token in session storage
  }

  authToken = data.token; //store authentication token in memory
  return data;
}

export async function signUp(
  request: SignUpRequest
): Promise<void> {
  await apiFetch<null>("/auth/signup", {
    method: "POST",
    body: JSON.stringify({
      username: request.name,
      email: request.email,
      password: request.password,
    }),
  });
}

export function isAuthenticated(): boolean {
  return false;
}