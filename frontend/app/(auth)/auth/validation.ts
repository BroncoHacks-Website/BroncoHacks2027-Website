export type SubmitState = "idle" | "submitting" | "success" | "error";

export type SignInFormValues = {
  email: string;
  password: string;
};

export type SignUpFormValues = {
  name: string;
  email: string;
  password: string;
};

export type SignInErrors = {
  email: string;
  password: string;
};

export type SignUpErrors = {
  name: string;
  email: string;
  password: string;
};

function validateEmail(email: string) {
  if (!email) return "Email is required.";
  if (!email.includes("@")) return "Enter a valid email address.";
  return "";
}

function validatePassword(password: string) {
  if (!password) return "Password is required.";

  if (password.length < 8) return "Password must be at least 8 characters.";

  if (!/.*[A-Z].*/.test(password)) return "Password must contain at least one uppercase letter.";

  if(!/.*[0-9].*/.test(password)) return "Password must contain at least one number.";

  if(!/.*[!@#$%^&*].*/.test(password)) return "Password must contain at least one special character.";

  return "";
}

export function validateSignIn(values: SignInFormValues): SignInErrors {
  return {
    email: validateEmail(values.email),
    password: validatePassword(values.password),
  };
}

export function validateSignUp(values: SignUpFormValues): SignUpErrors {
  return {
    name: values.name ? "" : "Name is required.",
    email: validateEmail(values.email),
    password: validatePassword(values.password),
  };
}

export function hasValidationErrors(errors: Record<string, string>) {
  return Object.values(errors).some(Boolean);
}