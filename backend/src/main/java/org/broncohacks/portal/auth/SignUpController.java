package org.broncohacks.portal.auth;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class SignUpController {
  private final AuthService authService;

  public SignUpController(AuthService authService) {
    this.authService = authService;
  }

  @PostMapping("/signup")
  public ResponseEntity<AuthResponse> signIn(@Valid @RequestBody SignUpRequest request) {
    AuthResponse response = authService.signUp(request);
    if (response.isSuccessful()) {
      return ResponseEntity.ok(response);
    }
    return ResponseEntity.badRequest().body(response);
  }
}
