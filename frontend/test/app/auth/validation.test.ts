import { describe, expect, it } from "vitest";
import {
  hasValidationErrors,
  validateSignIn,
  validateSignUp,
} from "../../../app/(auth)/auth/validation";

describe("auth validation", () => {
  it("returns errors for empty sign-in fields", () => {
    const errors = validateSignIn({
      email: "",
      password: "",
    });

    expect(errors.email).toBe("Email is required.");
    expect(errors.password).toBe("Password is required.");
    expect(hasValidationErrors(errors)).toBe(true);
  });

  it("returns an error for invalid email", () => {
    const errors = validateSignIn({
      email: "invalid-email",
      password: "password123",
    });

    expect(errors.email).toBe("Enter a valid email address.");
    expect(errors.password).toBe("");
    expect(hasValidationErrors(errors)).toBe(true);
  });

  it("returns an error for short password", () => {
    const errors = validateSignIn({
      email: "user@example.com",
      password: "123",
    });

    expect(errors.email).toBe("");
    expect(errors.password).toBe("Password must be at least 8 characters.");
    expect(hasValidationErrors(errors)).toBe(true);
  });

  it("passes valid sign-in values", () => {
    const errors = validateSignIn({
      email: "user@example.com",
      password: "password123",
    });

    expect(errors.email).toBe("");
    expect(errors.password).toBe("");
    expect(hasValidationErrors(errors)).toBe(false);
  });

  it("returns errors for empty sign-up fields", () => {
    const errors = validateSignUp({
      name: "",
      email: "",
      password: "",
    });

    expect(errors.name).toBe("Name is required.");
    expect(errors.email).toBe("Email is required.");
    expect(errors.password).toBe("Password is required.");
    expect(hasValidationErrors(errors)).toBe(true);
  });

  it("passes valid sign-up values", () => {
    const errors = validateSignUp({
      name: "Ahmad",
      email: "ahmad@example.com",
      password: "Password123!",
    });

    expect(errors.name).toBe("");
    expect(errors.email).toBe("");
    expect(errors.password).toBe("");
    expect(hasValidationErrors(errors)).toBe(false);
  });
});