package org.broncohacks.portal.auth;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class PasswordPolicies {
  private record Rule(String message, Predicate<String> test) {}

  private static final List<Rule> RULES =
      List.of(
          new Rule("Password must be at least 8 characters", password -> password.length() >= 8),
          new Rule(
              "Password must contain at least one uppercase letter",
              password -> password.matches(".*[A-Z].*")),
          new Rule(
              "Password must contain at least one number",
              password -> password.matches(".*[0-9].*")),
          new Rule(
              "Password must contain at least one special character",
              password -> password.matches(".*[!@#$%^&*].*")));

  public static Optional<String> validate(String password) {
    if (password == null || password.isBlank()) {
      return Optional.of("Password cannot be blank");
    }

    for (Rule rule : RULES) {
      if (!rule.test().test(password)) {
        return Optional.of(rule.message());
      }
    }

    return Optional.empty();
  }
}
