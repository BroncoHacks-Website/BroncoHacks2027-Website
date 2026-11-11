package org.broncohacks.portal.auth;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import org.broncohacks.portal.BroncoHacksPortalApplication;
import org.broncohacks.portal.JwtService;
import org.broncohacks.portal.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class EmailAuthIntegrationTest {
  private final UserRepository repository = new UserRepository();
  private final ObjectMapper mapper = new ObjectMapper();
  private List<User> originalUsers;
  private MockMvc mvc;

  @BeforeEach
  void setup() {
    originalUsers = new ArrayList<>(BroncoHacksPortalApplication.databaseUsers);
    BroncoHacksPortalApplication.databaseUsers.clear();
    AuthService service =
        new AuthServiceImpl(
            repository,
            new BCryptPasswordEncoder(),
            new JwtService("MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTIzNDU2Nzg5MDE="));
    mvc =
        MockMvcBuilders.standaloneSetup(
                new SignUpController(service), new SignInController(service))
            .setControllerAdvice(new AuthExceptionHandler())
            .build();
  }

  @AfterEach
  void cleanup() {
    BroncoHacksPortalApplication.databaseUsers.clear();
    BroncoHacksPortalApplication.databaseUsers.addAll(originalUsers);
  }

  @Test
  void repositoryNormalizesEmailAndDoesNotLookUpByName() {
    User user = new User("Bob", "hash", " BOB@Test.com ");
    repository.create(user);

    assertEquals("bob@test.com", user.getEmail());
    assertSame(user, repository.findByEmail(" BOB@Test.com ").orElseThrow());
    assertTrue(repository.findByEmail("Bob").isEmpty());
    assertTrue(repository.findByEmail(null).isEmpty());
    assertTrue(repository.findByEmail("missing@test.com").isEmpty());

    JsonNode json = mapper.valueToTree(user);
    assertEquals("Bob", json.get("name").asText());
    assertFalse(json.has("username"));
    assertFalse(json.has("pwHash"));
  }

  @Test
  void duplicateEmailsAreRejectedButDuplicateNamesAreAllowed() throws Exception {
    mvc.perform(
            post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {"name":"Bob","email":" BOB@Test.com ","password":"CorrectPass1!"}
                    """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.successful").value(true));

    mvc.perform(
            post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {"name":"Another Bob","email":"BOB@test.com","password":"CorrectPass1!"}
                    """))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("An account with this email already exists"));

    mvc.perform(
            post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {"name":"Bob","email":"other@test.com","password":"CorrectPass1!"}
                    """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.successful").value(true));
    assertEquals(2, BroncoHacksPortalApplication.databaseUsers.size());

    mvc.perform(
            post("/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                    {"email":" BOB@Test.com ","password":"CorrectPass1!"}
                    """))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.successful").value(true))
        .andExpect(jsonPath("$.data.token").isString());
  }

  @Test
  void invalidSignInFieldsAlwaysReturnGenericMessage() throws Exception {
    List<String> invalidSignInBodies =
        List.of(
            "{}",
            "{\"email\":\"invalid\",\"password\":\"CorrectPass1!\"}",
            "{\"email\":\" \",\"password\":\"CorrectPass1!\"}",
            "{\"email\":\"bob@test.com\",\"password\":\" \"}");

    for (String body : invalidSignInBodies) {
      mvc.perform(post("/auth/signin").contentType(MediaType.APPLICATION_JSON).content(body))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.successful").value(false))
          .andExpect(jsonPath("$.message").value("Email or password incorrect"));
    }
  }

  @Test
  void signUpRequiresNameEmailAndPassword() throws Exception {
    List<String> invalidSignUpBodies =
        List.of(
            "{}",
            "{\"name\":\" \",\"email\":\"bob@test.com\",\"password\":\"CorrectPass1!\"}",
            "{\"name\":\"Bob\",\"password\":\"CorrectPass1!\"}",
            "{\"name\":\"Bob\",\"email\":\"bob@test.com\"}");

    for (String body : invalidSignUpBodies) {
      mvc.perform(post("/auth/signup").contentType(MediaType.APPLICATION_JSON).content(body))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.successful").value(false));
    }
    assertTrue(BroncoHacksPortalApplication.databaseUsers.isEmpty());
  }
}
