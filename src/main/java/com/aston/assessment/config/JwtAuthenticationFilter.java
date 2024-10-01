package com.aston.assessment.config;


//import com.app.msc_project_administrator.token.TokenRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.beans.Transient;
import java.io.IOException;
import java.security.Security;

import jakarta.transaction.TransactionScoped;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtService jwtService;
  private final UserDetailsService userDetailsService;
//  private final TokenRepository tokenRepository;

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain
  ) throws ServletException, IOException {
    if (request.getServletPath().contains("/api/v1/auth")) {
      filterChain.doFilter(request, response);
      return;
    }
    final String authHeader = request.getHeader("Authorization");
    final String jwt;
    final String userEmail;
    if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
      filterChain.doFilter(request, response);
      return;
    }
    jwt = authHeader.substring(7);
    userEmail = jwtService.extractUsername(jwt);
    if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
      UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
//      var isTokenValid = tokenRepository.findByToken(jwt)
//          .map(t -> !t.isExpired() && !t.isRevoked())
//          .orElse(false);
      if (jwtService.isTokenValid(jwt, userDetails)) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
            userDetails,
            null,
            userDetails.getAuthorities()
        );
        authToken.setDetails(
            new WebAuthenticationDetailsSource().buildDetails(request)
        );
        SecurityContextHolder.getContext().setAuthentication(authToken);
      }
    }
    filterChain.doFilter(request, response);
  }
}
//
//
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.Cookie;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.antlr.v4.runtime.misc.NotNull;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//
//@Component
//public class JwtAuthenticationFilter extends OncePerRequestFilter {
//
//  @Autowired
//  private JwtService jwtService;
//
//  @Autowired
//  private UserDetailsService userDetailsService;
//
//  @Override
//  protected void doFilterInternal(@NotNull HttpServletRequest request,@NotNull HttpServletResponse response,@NotNull FilterChain filterChain)
//          throws ServletException, IOException {
//    final String jwt = extractJwtFromCookie(request);
//    final String userEmail;
//
//    if (jwt == null) {
//      filterChain.doFilter(request, response);
//      return;
//    }
//
//    userEmail = jwtService.extractUsername(jwt);
//    if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//      UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
//      if (jwtService.isTokenValid(jwt, userDetails)) {
//        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
//                userDetails,
//                null,
//                userDetails.getAuthorities()
//        );
//        authToken.setDetails(
//                new WebAuthenticationDetailsSource().buildDetails(request)
//        );
//        SecurityContextHolder.getContext().setAuthentication(authToken);
//      }
//    }
//    filterChain.doFilter(request, response);
//  }
//
//  private String extractJwtFromCookie(HttpServletRequest request) {
//    Cookie[] cookies = request.getCookies();
//    if (cookies != null) {
//      for (Cookie cookie : cookies) {
//        if ("token".equals(cookie.getName())) {
//          return cookie.getValue();
//        }
//      }
//    }
//    return null;
//  }
//}