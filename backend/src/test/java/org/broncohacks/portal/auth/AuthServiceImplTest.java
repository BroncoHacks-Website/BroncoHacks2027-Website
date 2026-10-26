package org.broncohacks.portal.auth;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import org.broncohacks.portal.JwtService;
import org.broncohacks.portal.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {
  @Spy private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
  @Mock private UserRepository userRepository;
  @Mock private JwtService jwtService;

  @InjectMocks private AuthServiceImpl authService;

  @Test
  void signUpFailsWhenUsernameAlreadyExists() {
    when(userRepository.findByUsername("bob"))
        .thenReturn(Optional.of(new User("bob", "somehash", "bob@test.com")));

    SignUpRequest request = new SignUpRequest();
    request.setUsername("bob");
    request.setPassword("Abcdefg1!");
    request.setEmail("bob@test.com");

    AuthResponse response = authService.signUp(request);

    assertFalse(response.isSuccessful());
    assertEquals("Username already taken", response.getMessage());
    verify(userRepository, never()).create(any());
  }

  @Test
  void signUpFailsWhenPasswordViolatesPolicy() {
    when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());

    SignUpRequest request = new SignUpRequest();
    request.setUsername("newuser");
    request.setPassword("weak");
    request.setEmail("new@test.com");

    AuthResponse response = authService.signUp(request);

    assertFalse(response.isSuccessful());
    verify(userRepository, never()).create(any());
  }

  @Test
  void signUpSucceedsAndSavesUser() {
    when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());

    SignUpRequest request = new SignUpRequest();
    request.setUsername("newuser");
    request.setPassword("Abcdefg1!");
    request.setEmail("new@test.com");

    AuthResponse response = authService.signUp(request);

    assertTrue(response.isSuccessful());
    verify(userRepository, times(1)).create(any(User.class));
  }

  @Test
  void signUpReturnsBadRequestOnFail() {
    when(userRepository.findByUsername("bob"))
        .thenReturn(Optional.of(new User("bob", "hash", "bob@test.com")));

    SignUpRequest req = new SignUpRequest();
    req.setUsername("bob");
    req.setPassword("Abcdefg1!");
    req.setEmail("bob@test.com");

    ResponseEntity<AuthResponse> res = new SignUpController(authService).signIn(req);
    assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());
    assertEquals("Username already taken", res.getBody().getMessage());
  }

  @Test
  void signInReturnsToken() {
    String realHash = passwordEncoder.encode("CorrectPass1!");
    when(userRepository.findByUsername("bob"))
        .thenReturn(Optional.of(new User("bob", realHash, "bob@test.com")));
    when(jwtService.createToken("bob")).thenReturn("signed-token");

    SignInRequest request = new SignInRequest();
    request.setUsername("bob");
    request.setPassword("CorrectPass1!");

    AuthResponse response = authService.signIn(request);

    assertTrue(response.isSuccessful());
    assertEquals("signed-token", response.getToken());
  }

  @Test
  void signInFailsWithWrongPassword() {
    String realHash =
        new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder()
            .encode("CorrectPass1!");
    when(userRepository.findByUsername("bob"))
        .thenReturn(Optional.of(new User("bob", realHash, "bob@test.com")));

    SignInRequest request = new SignInRequest();
    request.setUsername("bob");
    request.setPassword("WrongPassword!");

    AuthResponse response = authService.signIn(request);

    assertFalse(response.isSuccessful());
    assertEquals("Username or password incorrect", response.getMessage());
  }
}
