package com.example.jwtusermanagement.jwt;

import com.example.jwtusermanagement.security.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
public class AuthFilter extends OncePerRequestFilter {

   private final CustomUserDetailsService customUserDetailsService;
   private final JwtUtil jwtUtil;

    public AuthFilter(CustomUserDetailsService customUserDetailsService, JwtUtil jwtUtil) {
    this.customUserDetailsService = customUserDetailsService;
    this.jwtUtil = jwtUtil;
}
@Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                  FilterChain filterChain) throws ServletException, IOException {
  final String requestHeader = request.getHeader("Authorization");
    String username = null;
    String token = null;
    if (requestHeader != null && requestHeader.startsWith("Bearer")) {
        token = requestHeader.substring(7);
        username = jwtUtil.extractUsername(token);
    }
    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
        if (jwtUtil.validateToken(token, userDetails)){
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
    }
    filterChain.doFilter(request, response);
    }
    }
