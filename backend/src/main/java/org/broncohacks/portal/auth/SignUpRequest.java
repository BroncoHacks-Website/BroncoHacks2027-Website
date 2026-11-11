package org.broncohacks.portal.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Locale;

public class SignUpRequest {
  @NotBlank(message = "Name cannot be blank")
  private String name;

  @NotBlank(message = "Email cannot be blank")
  @Email(message = "Email must be valid")
  private String email;

  @NotBlank(message = "Password cannot be blank")
  @Size(min = 8, message = "Password must be at least 8 characters")
  private String password;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email == null ? null : email.trim().toLowerCase(Locale.ROOT);
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}
