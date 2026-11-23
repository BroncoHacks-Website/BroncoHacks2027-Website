package org.broncohacks.portal.auth;

import java.util.Locale;
import java.util.Optional;
import org.broncohacks.portal.BroncoHacksPortalApplication;
import org.broncohacks.portal.User;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository {
  public Optional<User> findByEmail(String email) {
    if (email == null) {
      return Optional.empty();
    }

    String normalized = email.trim().toLowerCase(Locale.ROOT);
    return BroncoHacksPortalApplication.databaseUsers.stream()
        .filter(user -> user.getEmail().equals(normalized))
        .findFirst();
  }

  public void create(User user) {
    BroncoHacksPortalApplication.databaseUsers.add(user);
  }
}
