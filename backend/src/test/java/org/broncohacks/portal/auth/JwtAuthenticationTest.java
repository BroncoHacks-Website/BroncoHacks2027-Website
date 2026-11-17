package org.broncohacks.portal.auth;

import static org.broncohacks.portal.TestConstants.JWT_SECRET;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import org.broncohacks.portal.JwtService;
import org.broncohacks.portal.SecurityConfig;
import org.broncohacks.portal.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(MeController.class)
@Import({SecurityConfig.class, JwtService.class})
@TestPropertySource(properties = "jwt.secret=" + JWT_SECRET)
class JwtAuthenticationTest {
  @Autowired private MockMvc mvc;
  @Autowired private JwtService jwtService;
  @MockBean private UserRepository userRepository;

  private User user;

  @BeforeEach
  void setUp() {
    user = new User("Bob", "never-expose-this", "bob@test.com");
    when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
  }

  @Test
  void validTokenReturnsCurrentUserWithoutPasswordHash() throws Exception {
    mvc.perform(
            get("/auth/me")
                .header("Authorization", "Bearer " + jwtService.createToken(user.getEmail())))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(user.getId().toString()))
        .andExpect(jsonPath("$.name").value("Bob"))
        .andExpect(jsonPath("$.email").value("bob@test.com"))
        .andExpect(jsonPath("$.pwHash").doesNotExist())
        .andExpect(jsonPath("$.password").doesNotExist());
  }

  @Test
  void missingTokenReturnsUnauthorized() throws Exception {
    mvc.perform(get("/auth/me")).andExpect(status().isUnauthorized());
  }

  @Test
  void expiredTokenReturnsUnauthorized() throws Exception {
    String token =
        Jwts.builder()
            .subject(user.getEmail())
            .expiration(Date.from(Instant.now().minusSeconds(60)))
            .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(JWT_SECRET)))
            .compact();

    mvc.perform(get("/auth/me").header("Authorization", "Bearer " + token))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void tamperedTokenReturnsUnauthorized() throws Exception {
    String token = jwtService.createToken(user.getEmail());
    int signatureStart = token.lastIndexOf('.') + 1;
    char replacement = token.charAt(signatureStart) == 'A' ? 'B' : 'A';
    String tampered =
        token.substring(0, signatureStart) + replacement + token.substring(signatureStart + 1);

    mvc.perform(get("/auth/me").header("Authorization", "Bearer " + tampered))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void tokenForMissingUserReturnsUnauthorized() throws Exception {
    when(userRepository.findByEmail("deleted@test.com")).thenReturn(Optional.empty());

    mvc.perform(
            get("/auth/me")
                .header("Authorization", "Bearer " + jwtService.createToken("deleted@test.com")))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void malformedTokenReturnsUnauthorized() throws Exception {
    mvc.perform(get("/auth/me").header("Authorization", "Bearer not-a-jwt"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void otherRoutesAlsoRequireAuthentication() throws Exception {
    mvc.perform(get("/api/teams")).andExpect(status().isUnauthorized());
  }
}
