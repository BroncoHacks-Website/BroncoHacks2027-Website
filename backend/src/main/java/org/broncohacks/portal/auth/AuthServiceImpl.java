package org.broncohacks.portal.auth;

import java.util.Map;
import java.util.Optional;
import org.broncohacks.portal.JwtService;
import org.broncohacks.portal.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;

  public AuthServiceImpl(
      UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtService = jwtService;
  }

  @Override
  public AuthResponse signUp(SignUpRequest request) {
    if (userRepository.findByEmail(request.getEmail()).isPresent()) {
      return new AuthResponse(false, "An account with this email already exists", null);
    }

    Optional<String> validationError = PasswordPolicies.validate(request.getPassword());
    if (validationError.isPresent()) {
      return new AuthResponse(false, validationError.get(), null);
    }

    User user =
        new User(
            request.getName(), passwordEncoder.encode(request.getPassword()), request.getEmail());
    userRepository.create(user);
    return new AuthResponse(true, "Account created successfully", null);
  }

  @Override
  public AuthResponse signIn(SignInRequest request) {
    Optional<User> user = userRepository.findByEmail(request.getEmail());
    if (user.isEmpty()
        || request.getPassword() == null
        || !passwordEncoder.matches(request.getPassword(), user.get().getPwHash())) {
      return new AuthResponse(false, "Email or password incorrect", null);
    }

    String token = jwtService.createToken(user.get().getEmail());
    return new AuthResponse(true, "Signed in successfully", Map.of("token", token));
  }
}
