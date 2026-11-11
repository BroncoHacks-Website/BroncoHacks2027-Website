package org.broncohacks.portal.auth;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class PasswordPoliciesTest {

  @Test
  void rejectsPasswordShorterThanEightCharacters() {
    String res = PasswordPolicies.validate("Ab1!");
    assertEquals("Password must be at least 8 characters", res);
  }

  @Test
  void rejectsPasswordWithoutUppercase() {
    String res = PasswordPolicies.validate("abcdefg1!");
    assertEquals("Password must contain at least one uppercase letter", res);
  }

  @Test
  void rejectsPasswordWithoutNumber() {
    String res = PasswordPolicies.validate("Abcdefgh!");
    assertEquals("Password must contain at least one number", res);
  }

  @Test
  void acceptsValidPassword() {
    String res = PasswordPolicies.validate("Abcdefg1!");
    assertNull(res);
  }
}
