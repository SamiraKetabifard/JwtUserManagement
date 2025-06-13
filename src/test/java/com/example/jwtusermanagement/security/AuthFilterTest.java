package com.example.jwtusermanagement.security;

import com.example.jwtusermanagement.entity.Role;
import com.example.jwtusermanagement.entity.User;
import com.example.jwtusermanagement.jwt.AuthFilter;
import com.example.jwtusermanagement.jwt.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthFilterTest {

    @Mock
    private CustomUserDetailsService customUserDetailsService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private AuthFilter authFilter;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_ValidToken_SetsAuthentication() throws ServletException, IOException {
        // Arrange
        String token = "validToken";
        String username = "samira.reza@gmail.com";
        User user = new User(1L, username, "password", Role.USER);
        UserDetails userDetails = new CustomUserDetails(user);
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtUtil.extractUsername(token)).thenReturn(username);
        when(customUserDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        when(jwtUtil.validateToken(token, userDetails)).thenReturn(true);
        // Act
        authFilter.doFilter(request, response, filterChain);
        // Assert
        verify(jwtUtil).extractUsername(token);
        verify(customUserDetailsService).loadUserByUsername(username);
        verify(jwtUtil).validateToken(token, userDetails);
        UsernamePasswordAuthenticationToken authentication =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertEquals(username, authentication.getName());
        assertEquals(1, authentication.getAuthorities().size());
        assertEquals("ROLE_USER", authentication.getAuthorities().iterator().next().getAuthority());
        verify(filterChain).doFilter(request, response);
    }
    @Test
    void doFilterInternal_InvalidToken_DoesNotSetAuthentication() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("Bearer invalidToken");
        when(jwtUtil.extractUsername("invalidToken")).thenReturn(null);
        // Act
        authFilter.doFilter(request, response, filterChain);
        // Assert
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }
    @Test
    void doFilterInternal_ExpiredToken_DoesNotSetAuthentication() throws ServletException, IOException {
        // Arrange
        String token = "expiredToken";
        String username = "mari.samira@gmail.com";
        User user = new User(2L, username, "password", Role.ADMIN);
        UserDetails userDetails = new CustomUserDetails(user);
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtUtil.extractUsername(token)).thenReturn(username);
        when(customUserDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        when(jwtUtil.validateToken(token, userDetails)).thenReturn(false);
        // Act
        authFilter.doFilter(request, response, filterChain);
        // Assert
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }
    @Test
    void doFilterInternal_NoToken_ContinuesFilterChain() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn(null);
        // Act
        authFilter.doFilter(request, response, filterChain);
        // Assert
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }
    @Test
    void doFilterInternal_MalformedTokenHeader_ContinuesFilterChain() throws ServletException, IOException {
        // Arrange
        when(request.getHeader("Authorization")).thenReturn("InvalidTokenFormat");
        // Act
        authFilter.doFilter(request, response, filterChain);
        // Assert
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }
    @Test
    void doFilterInternal_ValidTokenWithAdminRole_SetsCorrectAuthorities() throws ServletException, IOException {
        // Arrange
        String token = "adminToken";
        String username = "admin.mari@gmail.com";
        User user = new User(3L, username, "adminPass", Role.ADMIN);
        UserDetails userDetails = new CustomUserDetails(user);
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtUtil.extractUsername(token)).thenReturn(username);
        when(customUserDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        when(jwtUtil.validateToken(token, userDetails)).thenReturn(true);
        // Act
        authFilter.doFilter(request, response, filterChain);
        // Assert
        UsernamePasswordAuthenticationToken authentication =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertEquals(username, authentication.getName());
        assertEquals(1, authentication.getAuthorities().size());
        assertEquals("ROLE_ADMIN", authentication.getAuthorities().iterator().next().getAuthority());
        verify(filterChain).doFilter(request, response);
    }
}