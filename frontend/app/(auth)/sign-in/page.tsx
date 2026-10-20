"use client";

import { signIn } from "../../../lib/auth-client";
import { useState } from "react";
import AuthCard from "../auth/AuthCard";
import AuthInput from "../auth/AuthInput";
import AuthSubmitButton from "../auth/AuthSubmitButton";
import {
  hasValidationErrors,
  SubmitState,
  validateSignIn,
} from "../auth/validation";

export default function SignInPage() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  const [errors, setErrors] = useState({
    email: "",
    password: "",
  });

  const [submitState, setSubmitState] = useState<SubmitState>("idle");

  const handleSubmit = async (event: React.FormEvent) => {
    event.preventDefault();

    const newErrors = validateSignIn({
      email,
      password,
    });

    setErrors(newErrors);

    if (hasValidationErrors(newErrors)) {
      setSubmitState("error");
      return;
    }

    try {
      setSubmitState("submitting");

      await new Promise((resolve) => setTimeout(resolve, 2000));

      setSubmitState("success");
    } catch {
      setSubmitState("error");
    }
  };

  return (
    <AuthCard
      title="Welcome Back"
      subtitle="Sign in to your account to continue"
    >
      <form className="form" onSubmit={handleSubmit}>
        <AuthInput
          label="Email"
          type="email"
          placeholder="you@example.com"
          autoComplete="email"
          value={email}
          onChange={(value) => {
            setEmail(value);
            setSubmitState("idle");
          }}
          error={errors.email}
        />

        <AuthInput
          label="Password"
          type="password"
          placeholder="••••••••"
          autoComplete="current-password"
          value={password}
          onChange={(value) => {
            setPassword(value);
            setSubmitState("idle");
          }}
          error={errors.password}
        />

        <AuthSubmitButton
          isSubmitting={submitState === "submitting"}
          loadingText="Signing in..."
          defaultText="Sign in"
        />

        {submitState === "success" && (
          <p className="status-open">Successfully signed in.</p>
        )}

        {submitState === "error" && (
          <p className="status-full">Please fix the highlighted errors.</p>
        )}
      </form>
    </AuthCard>
  );
}