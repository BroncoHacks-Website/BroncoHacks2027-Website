package org.broncohacks.portal.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AuthExceptionHandler {
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<AuthResponse> handleValidation(MethodArgumentNotValidException ex) {
    // return "Email or password incorrect" for any failed sign-in
    String firstError =
        ex.getBindingResult().getTarget() instanceof SignInRequest
            ? "Email or password incorrect"
            : ex.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
    AuthResponse error = new AuthResponse(false, firstError, null);
    return ResponseEntity.badRequest().body(error);
  }
}
