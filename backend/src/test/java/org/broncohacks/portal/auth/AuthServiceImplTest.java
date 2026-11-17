package org.broncohacks.portal.auth;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
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
  void signUpFailsWhenEmailAlreadyExists() {
    when(userRepository.findByEmail("bob@test.com"))
        .thenReturn(Optional.of(new User("bob", "somehash", "bob@test.com")));

    SignUpRequest req = new SignUpRequest();
    req.setName("Another Bob");
    req.setPassword("Abcdefg1!");
    req.setEmail(" BOB@Test.com ");

    AuthResponse res = authService.signUp(req);

    assertFalse(res.isSuccessful());
    assertEquals("An account with this email already exists", res.getMessage());
    verify(userRepository, never()).create(any());
  }

  @Test
  void signUpFailsWhenPasswordViolatesPolicy() {
    when(userRepository.findByEmail("new@test.com")).thenReturn(Optional.empty());

    SignUpRequest req = new SignUpRequest();
    req.setName("newuser");
    req.setPassword("weak");
    req.setEmail(" NEW@Test.com ");

    AuthResponse res = authService.signUp(req);

    assertFalse(res.isSuccessful());
    verify(userRepository, never()).create(any());
  }

  @Test
  void signUpSucceedsAndSavesUser() {
    when(userRepository.findByEmail("new@test.com")).thenReturn(Optional.empty());

    SignUpRequest req = new SignUpRequest();
    req.setName("newuser");
    req.setPassword("Abcdefg1!");
    req.setEmail(" NEW@Test.com ");

    AuthResponse res = authService.signUp(req);

    assertTrue(res.isSuccessful());
    verify(userRepository)
        .create(
            argThat(
                user ->
                    user.getName().equals("newuser")
                        && user.getEmail().equals("new@test.com")
                        && passwordEncoder.matches("Abcdefg1!", user.getPwHash())));
  }

  @Test
  void signUpReturnsBadRequestOnFail() {
    when(userRepository.findByEmail("bob@test.com"))
        .thenReturn(Optional.of(new User("bob", "hash", "bob@test.com")));

    SignUpRequest req = new SignUpRequest();
    req.setName("bob");
    req.setPassword("Abcdefg1!");
    req.setEmail("bob@test.com");

    ResponseEntity<AuthResponse> res = new SignUpController(authService).signIn(req);
    assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());
    assertEquals("An account with this email already exists", res.getBody().getMessage());
  }

  @Test
  void signInReturnsToken() {
    String realHash = passwordEncoder.encode("CorrectPass1!");
    when(userRepository.findByEmail("bob@test.com"))
        .thenReturn(Optional.of(new User("bob", realHash, "bob@test.com")));
    when(jwtService.createToken("bob@test.com")).thenReturn("signed-token");

    SignInRequest req = new SignInRequest();
    req.setEmail(" BOB@Test.com ");
    req.setPassword("CorrectPass1!");

    AuthResponse res = authService.signIn(req);
    assertTrue(res.isSuccessful());
    assertEquals(Map.of("token", "signed-token"), res.getData());

    JsonNode json = new ObjectMapper().valueToTree(res);
    assertEquals(3, json.size());
    assertTrue(json.get("successful").asBoolean());
    assertEquals("Signed in successfully", json.get("message").asText());
    assertEquals("signed-token", json.get("data").get("token").asText());
    assertFalse(json.has("token"));
  }

  @Test
  void signInFailsWithWrongPassword() {
    String realHash =
        new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder()
            .encode("CorrectPass1!");
    when(userRepository.findByEmail("bob@test.com"))
        .thenReturn(Optional.of(new User("bob", realHash, "bob@test.com")));

    SignInRequest request = new SignInRequest();
    request.setEmail("bob@test.com");
    request.setPassword("WrongPassword!");

    AuthResponse res = authService.signIn(request);

    assertFalse(res.isSuccessful());
    assertEquals("Email or password incorrect", res.getMessage());
  }

  @Test
  void signInFailsWithUnknownEmail() {
    when(userRepository.findByEmail("missing@test.com")).thenReturn(Optional.empty());
    SignInRequest request = new SignInRequest();
    request.setEmail("missing@test.com");
    request.setPassword("CorrectPass1!");

    AuthResponse res = authService.signIn(request);

    assertFalse(res.isSuccessful());
    assertEquals("Email or password incorrect", res.getMessage());
    assertNull(res.getData());
    verifyNoInteractions(jwtService);
  }

  @Test
  void signInFailsWithNullPassword() {
    when(userRepository.findByEmail("bob@test.com"))
        .thenReturn(Optional.of(new User("Bob", "hash", "bob@test.com")));
    SignInRequest req = new SignInRequest();
    req.setEmail("bob@test.com");

    AuthResponse res = authService.signIn(req);

    assertFalse(res.isSuccessful());
    assertEquals("Email or password incorrect", res.getMessage());
    verifyNoInteractions(jwtService);
  }
}
