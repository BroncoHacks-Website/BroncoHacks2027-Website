import { afterEach, describe, expect, it, vi } from "vitest";

import { API_BASE_URL, ApiError, apiFetch } from "../../lib/api-client";

afterEach(() => {
  vi.unstubAllGlobals();
});

describe("apiFetch", () => {
  it("returns the response data on success", async () => {
    const fetchMock = vi.fn().mockResolvedValue({
      ok: true,
      status: 200,
      text: vi.fn().mockResolvedValue(
        JSON.stringify({
          successful: true,
          message: "ok",
          data: { id: 42, name: "Ada" },
        })
      ),
    });

    vi.stubGlobal("fetch", fetchMock);

    await expect(apiFetch("/users")).resolves.toEqual({ id: 42, name: "Ada" });

    expect(fetchMock).toHaveBeenCalledTimes(1);
    expect(fetchMock.mock.calls[0][0]).toBe(`${API_BASE_URL}/users`);
    expect(fetchMock.mock.calls[0][1]?.headers?.get("Accept")).toBe("application/json");
  });

  it("throws ApiError with the backend message when the API reports an error", async () => {
    const fetchMock = vi.fn().mockResolvedValue({
      ok: false,
      status: 401,
      text: vi.fn().mockResolvedValue(
        JSON.stringify({
          successful: false,
          message: "Invalid credentials",
          data: null,
        })
      ),
    });

    vi.stubGlobal("fetch", fetchMock);

    const promise = apiFetch("/login", {
      method: "POST",
      body: JSON.stringify({ username: "abc", password: "wrong" }),
    });

    await expect(promise).rejects.toBeInstanceOf(ApiError);
    await expect(promise).rejects.toMatchObject({ message: "Invalid credentials", status: 401 });
  });

  it("throws ApiError on network failures", async () => {
    const fetchMock = vi.fn().mockRejectedValue(new TypeError("Failed to fetch"));

    vi.stubGlobal("fetch", fetchMock);

    await expect(apiFetch("/health")).rejects.toMatchObject({
      name: "ApiError",
      message: "Failed to fetch",
    });
  });
});
