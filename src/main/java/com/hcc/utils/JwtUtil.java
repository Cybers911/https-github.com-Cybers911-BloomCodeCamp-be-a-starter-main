package com.hcc.utils;

import com.hcc.entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class JwtUtil implements Serializable {
    private static final long serialVersionUID = -2550185165626007488L;

    // Token validity period (5 days in seconds)
    public static final long JWT_TOKEN_VALIDITY = 5 * 24 * 60 * 60;

    @Value("${jwt.secret}")
    private String secret;

    /**
     * Extract username (subject) from the token.
     * Usage: Equivalent to `extractUsername` in your request.
     */
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject); // Extract 'sub' from claims
    }

    /**
     * Extract the issued-at date from the token.
     */
    public Date getIssuedAtDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getIssuedAt);
    }

    /**
     * Extract the expiration date from the token.
     */
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    /**
     * Retrieve a specific claim from the token.
     * @param <T> type of the claim
     */
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Retrieve all claims from the token using the secret key.
     */
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Check if the token is expired.
     * @return true if expired; false otherwise.
     */
    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    /**
     * Generate a JWT token for the given user details.
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("authorities", userDetails.getAuthorities()
                .stream()
                .map(auth -> auth.getAuthority())
                .collect(Collectors.toList()));

        return doGenerateToken(claims, userDetails.getUsername());
    }

    /**
     * Generate JWT token with claims and subject.
     */
    private String doGenerateToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject) // The subject is typically the username
                .setIssuedAt(new Date(System.currentTimeMillis())) // Token issued at time
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000)) // Token expiry
                .signWith(SignatureAlgorithm.HS256, secret) // Sign with a secure key
                .compact();
    }

    /**
     * Validate the token for a given user.
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}