package org.broncohacks.portal.auth;

import static org.broncohacks.portal.TestConstants.JWT_SECRET;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.broncohacks.portal.JwtService;
import org.broncohacks.portal.SecurityConfig;
import org.broncohacks.portal.Team;
import org.broncohacks.portal.TeamController;
import org.broncohacks.portal.TeamRepository;
import org.broncohacks.portal.TeamServiceImpl;
import org.broncohacks.portal.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest({MeController.class, TeamController.class})
@Import({SecurityConfig.class, JwtService.class, TeamServiceImpl.class})
@TestPropertySource(properties = "jwt.secret=" + JWT_SECRET)
class JwtAuthenticationTest {
  @Autowired private MockMvc mvc;
  @Autowired private JwtService jwtService;
  @MockBean private UserRepository userRepository;
  @MockBean private TeamRepository teamRepository;

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
        .andExpect(jsonPath("$.successful").value(true))
        .andExpect(jsonPath("$.message").value("Current user retrieved successfully"))
        .andExpect(jsonPath("$.data.id").value(user.getId().toString()))
        .andExpect(jsonPath("$.data.name").value("Bob"))
        .andExpect(jsonPath("$.data.email").value("bob@test.com"))
        .andExpect(jsonPath("$.data.pwHash").doesNotExist())
        .andExpect(jsonPath("$.data.password").doesNotExist());
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

  @Test
  void joinUsesTokenUserEvenWhenBodySuppliesAnotherEmail() throws Exception {
    Team team = new Team("Test team");
    User target = new User("Alice", "hash", "alice@test.com");

    when(teamRepository.findByInviteCode(team.getInviteCode())).thenReturn(Optional.of(team));

    mvc.perform(
            post("/api/teams/join")
                .header("Authorization", "Bearer " + jwtService.createToken(user.getEmail()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "inviteCode": "%s",
                      "newMemberEmail": "%s"
                    }
                    """
                        .formatted(team.getInviteCode(), target.getEmail())))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.successful").value(true));

    assertEquals(List.of(user), team.getMembers());
  }

  @Test
  void leaveUsesTokenUserEvenWhenBodySuppliesAnotherEmail() throws Exception {
    Team team = new Team("Test team");
    User target = new User("Alice", "hash", "alice@test.com");
    team.addMember(user);
    team.addMember(target);

    when(teamRepository.findByName(team.getTeamName())).thenReturn(Optional.of(team));

    mvc.perform(
            post("/api/teams/leave")
                .header("Authorization", "Bearer " + jwtService.createToken(user.getEmail()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {
                      "teamName": "test team",
                      "email": "alice@test.com"
                    }
                    """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.successful").value(true));

    assertEquals(List.of(target), team.getMembers());
  }

  @Test
  void joinAndLeaveRequireAuthentication() throws Exception {
    mvc.perform(
            post("/api/teams/join")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {"inviteCode": "some-code"}
                    """))
        .andExpect(status().isUnauthorized());

    mvc.perform(
            post("/api/teams/leave")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {"teamName": "test team"}
                    """))
        .andExpect(status().isUnauthorized());
  }
}
