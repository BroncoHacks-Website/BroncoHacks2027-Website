import {  describe, expect, it, beforeEach, vi } from "vitest";
import { fireEvent, render, screen, waitFor } from "@testing-library/react";
import SignUpPage from "../../../app/(auth)/sign-up/page";

vi.mock("../../../lib/api-client", () => ({
  apiFetch: vi.fn().mockResolvedValue(null),
}));

describe("SignUpPage submit states", () => {
  beforeEach(() => {
    localStorage.clear();
  });

  it("shows error state when submitting empty form", () => {
    render(<SignUpPage />);

    fireEvent.click(screen.getByRole("button", { name: "Sign Up" }));

    expect(screen.getByText("Name is required.")).toBeInTheDocument();
    expect(screen.getByText("Email is required.")).toBeInTheDocument();
    expect(screen.getByText("Password is required.")).toBeInTheDocument();
    expect(
      screen.getByText("Please fix the highlighted errors.")
    ).toBeInTheDocument();
  });

  it("shows submitting state and then success state", async () => {
    render(<SignUpPage />);

    fireEvent.change(screen.getByPlaceholderText("John Doe"), {
      target: { value: "Ahmad" },
    });

    fireEvent.change(screen.getByPlaceholderText("you@example.com"), {
      target: { value: "ahmad@example.com" },
    });

    fireEvent.change(screen.getByPlaceholderText("••••••••"), {
      target: { value: "Password123!" },
    });

    fireEvent.click(screen.getByRole("button", { name: "Sign Up" }));

    expect(
      screen.getByRole("button", { name: "Signing Up..." })
    ).toBeDisabled();

    await waitFor(
      () => {
        expect(
          screen.getByText("Account created successfully.")
        ).toBeInTheDocument();
      },
      { timeout: 3000 }
    );
  });

  it("returns to idle state when user edits after an error", () => {
    render(<SignUpPage />);

    fireEvent.click(screen.getByRole("button", { name: "Sign Up" }));

    expect(
      screen.getByText("Please fix the highlighted errors.")
    ).toBeInTheDocument();

    fireEvent.change(screen.getByPlaceholderText("John Doe"), {
      target: { value: "Ahmad" },
    });

    expect(
      screen.queryByText("Please fix the highlighted errors.")
    ).not.toBeInTheDocument();
  });
});