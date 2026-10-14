export const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL ?? "http://localhost:8080";

export class ApiError extends Error {
  status?: number;

  constructor(message: string, status?: number) {
    super(message);
    this.name = "ApiError";
    this.status = status;
  }
}

export type ApiEnvelope<T> = {
  successful: boolean;
  message: string;
  data: T | null;
};

export async function apiFetch<T>(
  path: string,
  options: RequestInit = {}
): Promise<T> {
  const requestUrl = new URL(path, API_BASE_URL).toString();
  const headers = new Headers(options.headers ?? {});

  if (options.body !== undefined && options.body !== null && !headers.has("Content-Type")) {
    headers.set("Content-Type", "application/json");
  }

  if (!headers.has("Accept")) {
    headers.set("Accept", "application/json");
  }

  try {
    const response = await fetch(requestUrl, {
      ...options,
      headers,
    });

    const text = await response.text();
    const payload = text ? (JSON.parse(text) as ApiEnvelope<T> | T) : null;

    if (!response.ok || (payload && typeof payload === "object" && "successful" in payload && payload.successful === false)) {
      const message =
        payload && typeof payload === "object" && "message" in payload && typeof payload.message === "string" && payload.message.trim()
          ? payload.message
          : `Request failed with status ${response.status}`;

      throw new ApiError(message, response.status);
    }

    if (!payload || typeof payload !== "object" || !("successful" in payload)) {
      return payload as T;
    }

    return (payload as ApiEnvelope<T>).data as T;
  } catch (error) {
    if (error instanceof ApiError) {
      throw error;
    }

    const message = error instanceof Error ? error.message : "Network error";
    throw new ApiError(message);
  }
}

