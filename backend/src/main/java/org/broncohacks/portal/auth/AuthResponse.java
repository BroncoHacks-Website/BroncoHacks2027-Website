package org.broncohacks.portal.auth;

public class AuthResponse {
  private boolean successful;
  private String message;
  private Object data;

  public AuthResponse(boolean successful, String message, Object data) {
    this.successful = successful;
    this.message = message;
    this.data = data;
  }

  public boolean isSuccessful() {
    return successful;
  }

  public String getMessage() {
    return message;
  }

  public Object getData() {
    return data;
  }
}
