package org.broncohacks.portal;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.time.Duration;
import javax.crypto.SecretKey;
import org.junit.jupiter.api.Test;

class JwtServiceTest {
  private static final String SECRET = "MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTIzNDU2Nzg5MDE=";

  @Test
  void tokenContainsUsernameAndExpiresAfterOneDay() {
    String token = new JwtService(SECRET).createToken("bob");
    SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET));
    Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();

    assertEquals("bob", claims.getSubject());
    assertEquals(
        Duration.ofHours(24).toMillis(),
        claims.getExpiration().getTime() - claims.getIssuedAt().getTime());
  }
}
