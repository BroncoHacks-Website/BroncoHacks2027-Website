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

export async function signIn(
  request: SignInRequest
): Promise<AuthResult> {
  // Future backend integration:
  // POST /api/auth/sign-in

  await new Promise((resolve) => setTimeout(resolve, 1000));

  return {
    ok: true,
    data: {
      user: {
        id: "999",
        name: "Ahmad",
        email: request.email,
      },
      token: "fake-sign-in-token",
    },
  };
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