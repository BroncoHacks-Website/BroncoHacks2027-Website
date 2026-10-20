package org.broncohacks.portal;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class TestData implements CommandLineRunner {

    @Override
    public void run(String... args) {
        User testUser = new User(
                "testUser",
                "abc",
                "test@gmail.com"
        );

        BroncoHacksPortalApplication.databaseUsers.add(testUser);
    }
}
