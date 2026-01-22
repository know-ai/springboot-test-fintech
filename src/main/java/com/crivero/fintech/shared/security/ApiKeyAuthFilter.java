package com.crivero.fintech.shared.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

public class ApiKeyAuthFilter extends OncePerRequestFilter {
  public static final String API_KEY_HEADER = "X-API-KEY";

  private final String expectedApiKey;
  private final ObjectMapper objectMapper;

  public ApiKeyAuthFilter(String expectedApiKey, ObjectMapper objectMapper) {
    this.expectedApiKey = expectedApiKey;
    this.objectMapper = objectMapper;
  }

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    String path = request.getRequestURI();
    return path.startsWith("/swagger-ui")
        || path.startsWith("/v3/api-docs")
        || path.startsWith("/actuator");
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain)
      throws ServletException, IOException {

    String provided = request.getHeader(API_KEY_HEADER);
    if (provided == null || expectedApiKey == null || !expectedApiKey.equals(provided)) {
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
      objectMapper.writeValue(
          response.getWriter(),
          Map.of(
              "error", "UNAUTHORIZED",
              "message", "Missing or invalid API key",
              "header", API_KEY_HEADER));
      return;
    }

    SecurityContextHolder.getContext()
        .setAuthentication(
            new UsernamePasswordAuthenticationToken(
                "api-key", null, List.of(new SimpleGrantedAuthority("ROLE_API"))));
    filterChain.doFilter(request, response);
  }
}


