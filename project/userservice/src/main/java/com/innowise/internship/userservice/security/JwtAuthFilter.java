package com.innowise.internship.userservice.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.ArrayList;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

  private final WebClient.Builder webClientBuilder;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws ServletException, IOException {

      final String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

      if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
          chain.doFilter(request, response);
          return;
      }

      final String token = authorizationHeader.substring(7);

      Boolean isValid = webClientBuilder.build()
              .get()
              .uri("/validate?accessToken=" + token)
              .retrieve()
              .bodyToMono(Boolean.class)
              .block();

      if (Boolean.TRUE.equals(isValid)) {
        UsernamePasswordAuthenticationToken authenticationToken =
            new UsernamePasswordAuthenticationToken(null, null, new ArrayList<>());

        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
      }

      chain.doFilter(request, response);
  }
}
