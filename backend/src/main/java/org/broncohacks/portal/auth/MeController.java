package org.broncohacks.portal.auth;

import org.broncohacks.portal.PublicUser;
import org.broncohacks.portal.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MeController {
  @GetMapping("/auth/me")
  public PublicUser me(@AuthenticationPrincipal User user) {
    return new PublicUser(user.getId(), user.getName(), user.getEmail());
  }
}
