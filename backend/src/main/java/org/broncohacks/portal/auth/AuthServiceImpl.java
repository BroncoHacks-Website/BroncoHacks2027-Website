package org.broncohacks.portal.auth;

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
    // check username isn't taken, hash request.getPassword(),
    if (userRepository.findByUsername(request.getUsername()).isPresent()) {
      return new AuthResponse(false, "Username already taken", null);
    }
    String passwordValidation = PasswordPolicies.validate(request.getPassword());
    if (passwordValidation != null) {
      return new AuthResponse(false, passwordValidation, null);
    }

    String hashedPassword = passwordEncoder.encode(request.getPassword());

    // save a new User, return a success/failure AuthResponse
    User newUser = new User(request.getUsername(), hashedPassword, request.getEmail());
    userRepository.create(newUser);
    // TODO: return jwt if needed
    return new AuthResponse(true, "Account created successfully", null);
  }

  @Override
  public AuthResponse signIn(SignInRequest request) {
    Optional<User> user = userRepository.findByUsername(request.getUsername());
    // If user is empty, reject.
    if (user.isEmpty()) {
      return new AuthResponse(false, "Username Empty", null);
    }
    // Success!
    if (passwordEncoder.matches(request.getPassword(), user.get().getPwHash())) {
      String token = jwtService.createToken(user.get().getUsername());
      return new AuthResponse(true, "Signed in successfully", token);
    }
    // If password doesn't match user's hashedPW, reject.
    return new AuthResponse(false, "Username or password incorrect", null);
  }
}
