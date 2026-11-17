package org.broncohacks.portal.auth;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;
import org.junit.jupiter.api.Test;

class PasswordPoliciesTest {
  @Test
  void rejectsNullOrBlankPassword() {
    String[] invalidPasswords = {null, "", "  "};
    for (String password : invalidPasswords) {
      assertEquals(Optional.of("Password cannot be blank"), PasswordPolicies.validate(password));
    }
  }

  @Test
  void enforcesMinimumPasswordLength() {
    assertEquals(
        Optional.of("Password must be at least 8 characters"),
        PasswordPolicies.validate("Abcde1!"));
    assertEquals(Optional.empty(), PasswordPolicies.validate("Abcdef1!"));
  }

  @Test
  void rejectsPasswordWithoutUppercase() {
    assertEquals(
        Optional.of("Password must contain at least one uppercase letter"),
        PasswordPolicies.validate("abcdefg1!"));
  }

  @Test
  void rejectsPasswordWithoutNumber() {
    assertEquals(
        Optional.of("Password must contain at least one number"),
        PasswordPolicies.validate("Abcdefgh!"));
  }

  @Test
  void rejectsPasswordWithoutSpecialCharacter() {
    assertEquals(
        Optional.of("Password must contain at least one special character"),
        PasswordPolicies.validate("Abcdefg1"));
  }

  @Test
  void acceptsValidPassword() {
    assertTrue(PasswordPolicies.validate("Abcdefg1!").isEmpty());
  }
}
