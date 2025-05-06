package com.example.jwtusermanagement.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    @Value("${app.secret-key}")
    private String secretKey;

    @Value("${app.expiration-time}")
    private long expirationTime;
    
    // Get the signing key used to sign the JWT token
    private Key getSignKey() {
        return Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretKey));
    }
    // Extract all claims from the JWT token
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    // Retrieve a specific claim from the JWT token
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    // Create the JWT token with claims and subject
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    // Generate a JWT token for a user based on their details
    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username);
    }
    // Extract the username (subject) from the JWT token
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    // Extract the expiration date from the JWT token
    private Date extractExpirationDate(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    // Check if the token is expired
    private boolean isTokenExpired(String token) {
        return extractExpirationDate(token).before(new Date());
    }
    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            String username = extractUsername(token);
            return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
