package org.broncohacks.portal;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.broncohacks.portal.auth.UserRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
  private final JwtService jwtService;
  private final UserRepository userRepository;

  public JwtAuthenticationFilter(JwtService jwtService, UserRepository userRepository) {
    this.jwtService = jwtService;
    this.userRepository = userRepository;
  }

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
    String method = request.getMethod();
    String servletPath = request.getServletPath();

    return "POST".equals(method)
        && ("/auth/signup".equals(servletPath) || "/auth/signin".equals(servletPath));
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    String header = request.getHeader(HttpHeaders.AUTHORIZATION);
    if (header == null) {
      filterChain.doFilter(request, response);
      return;
    }
    if (!header.regionMatches(true, 0, "Bearer ", 0, 7)) {
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
      return;
    }

    try {
      String token = header.substring(7);
      String email = jwtService.validateAndGetEmail(token);
      Optional<User> user = userRepository.findByEmail(email);
      if (user.isEmpty()) {
        SecurityContextHolder.clearContext();
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        return;
      }

      var auth =
          UsernamePasswordAuthenticationToken.authenticated(user.get(), List.of(), List.of());
      var ctx = SecurityContextHolder.createEmptyContext();
      ctx.setAuthentication(auth);
      SecurityContextHolder.setContext(ctx);
    } catch (JwtException | IllegalArgumentException e) {
      SecurityContextHolder.clearContext();
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
      return;
    }

    filterChain.doFilter(request, response);
  }
}
