package org.broncohacks.portal.auth;

public class AuthResponse {
  private boolean successful;
  private String message;
  private String token;

  public AuthResponse(boolean successful, String message, String token) {
    this.successful = successful;
    this.message = message;
    this.token = token;
  }

  public boolean isSuccessful() {
    return successful;
  }

  public String getMessage() {
    return message;
  }

  public String getToken() {
    return token;
  }
}
