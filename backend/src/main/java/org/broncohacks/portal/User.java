package org.broncohacks.portal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Locale;

public class User {
  private String name;
  private String pwHash;
  private String email;

  public User(String name, String pwHash, String email) {
    this.name = name;
    this.pwHash = pwHash;
    this.email = email.trim().toLowerCase(Locale.ROOT);
  }

  public String getName() {
    return this.name;
  }

  // makes sure that the user's password hash does NOT get revealed in the JSON sent to frontend
  // :skull:
  @JsonIgnore
  public String getPwHash() {
    return this.pwHash;
  }

  public String getEmail() {
    return this.email;
  }
}
