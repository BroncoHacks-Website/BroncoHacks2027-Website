package org.broncohacks.portal.auth;

public interface AuthService {
  AuthResponse signUp(SignUpRequest request);

  AuthResponse signIn(SignInRequest request);
}
