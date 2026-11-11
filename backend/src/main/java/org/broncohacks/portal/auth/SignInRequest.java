package org.broncohacks.portal.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.Locale;

public class SignInRequest {
  @NotBlank(message = "Email cannot be blank")
  @Email(message = "Email must be valid")
  private String email;

  @NotBlank(message = "Password cannot be blank")
  private String password;

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email == null ? null : email.trim().toLowerCase(Locale.ROOT);
  }

  public String getPassword() {
    return this.password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}
