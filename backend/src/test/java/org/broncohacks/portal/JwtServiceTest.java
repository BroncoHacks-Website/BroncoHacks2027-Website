package org.broncohacks.portal;

import static org.broncohacks.portal.TestConstants.JWT_SECRET;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.time.Duration;
import javax.crypto.SecretKey;
import org.junit.jupiter.api.Test;

class JwtServiceTest {
  @Test
  void tokenContainsEmailAndExpiresAfterOneDay() {
    String token = new JwtService(JWT_SECRET).createToken("bob@test.com");
    SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(JWT_SECRET));
    Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();

    assertEquals("bob@test.com", claims.getSubject());
    assertEquals(
        Duration.ofHours(24).toMillis(),
        claims.getExpiration().getTime() - claims.getIssuedAt().getTime());
  }
}
