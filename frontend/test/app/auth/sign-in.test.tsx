import { describe, expect, it, beforeEach, vi } from "vitest";
import { fireEvent, render, screen, waitFor } from "@testing-library/react";
import SignInPage from "../../../app/(auth)/sign-in/page";
import { apiFetch } from "../../../lib/api-client";

vi.mock("next/navigation", () => ({
  useRouter: () => ({ push: vi.fn() }),
}));

vi.mock("../../../lib/api-client", () => ({
  apiFetch: vi.fn().mockResolvedValue({ token: "test-token" }),
}));

describe("SignInPage submit states", () => {
  beforeEach(() => {
    localStorage.clear();
  });

  it("shows error state when submitting empty form", () => {
    render(<SignInPage />);

    fireEvent.click(screen.getByRole("button", { name: "Sign in" }));

    expect(screen.getByText("Email is required.")).toBeInTheDocument();
    expect(screen.getByText("Password is required.")).toBeInTheDocument();
    expect(
      screen.getByText("Please fix the highlighted errors.")
    ).toBeInTheDocument();
  });

  it("shows submitting state and then success state", async () => {
    render(<SignInPage />);

    fireEvent.change(screen.getByPlaceholderText("you@example.com"), {
      target: { value: "ahmad@example.com" },
    });

    fireEvent.change(screen.getByPlaceholderText("••••••••"), {
      target: { value: "password123" },
    });

    fireEvent.click(screen.getByRole("button", { name: "Sign in" }));

    expect(
      screen.getByRole("button", { name: "Signing in..." })
    ).toBeDisabled();

    await waitFor(
      () => {
        expect(
          screen.getByText("Successfully signed in.")
        ).toBeInTheDocument();
      },
      { timeout: 3000 }
    );
  });

  it("returns to idle state when user edits after an error", () => {
    render(<SignInPage />);

    fireEvent.click(screen.getByRole("button", { name: "Sign in" }));

    expect(
      screen.getByText("Please fix the highlighted errors.")
    ).toBeInTheDocument();

    fireEvent.change(screen.getByPlaceholderText("you@example.com"), {
      target: { value: "ahmad@example.com" },
    });

    expect(
      screen.queryByText("Please fix the highlighted errors.")
    ).not.toBeInTheDocument();
  });

  it("shows the backend's message when sign-in fails", async () => {
    vi.mocked(apiFetch).mockRejectedValueOnce(
      new Error("Email or password incorrect")
    );
    render(<SignInPage />);

    fireEvent.change(screen.getByPlaceholderText("you@example.com"), {
      target: { value: "ahmad@example.com" },
    });
    fireEvent.change(screen.getByPlaceholderText("••••••••"), {
      target: { value: "WrongPass1!" },
    });
    fireEvent.click(screen.getByRole("button", { name: "Sign in" }));

    await waitFor(() => {
      expect(
        screen.getByText("Email or password incorrect")
      ).toBeInTheDocument();
    });
  });
});
