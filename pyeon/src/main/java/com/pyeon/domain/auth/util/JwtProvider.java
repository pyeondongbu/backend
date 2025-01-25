package com.pyeon.domain.auth.util;

import com.pyeon.domain.auth.domain.UserPrincipal;
import com.pyeon.domain.auth.service.CustomUserDetailsService;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.security.SecurityException;

import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Component
@Slf4j
public class JwtProvider {

    private final String secretKey;
    private final long accessTokenValidityInMilliseconds;
    private final long refreshTokenValidityInMilliseconds;
    private final CustomUserDetailsService userDetailsService;

    public JwtProvider(
            @Value("${jwt.secret-key}") String secretKey,
            @Value("${jwt.access-token-validity}") long accessTokenValidityInMilliseconds,
            @Value("${jwt.refresh-token-validity}") long refreshTokenValidityInMilliseconds,
            CustomUserDetailsService userDetailsService
    ) {
        this.secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
        this.accessTokenValidityInMilliseconds = accessTokenValidityInMilliseconds;
        this.refreshTokenValidityInMilliseconds = refreshTokenValidityInMilliseconds;
        this.userDetailsService = userDetailsService;
    }

    public String createAccessToken(Authentication authentication) {
        return createToken(authentication, accessTokenValidityInMilliseconds);
    }

    public String createRefreshToken(Authentication authentication) {
        return createToken(authentication, refreshTokenValidityInMilliseconds);
    }

    private String createToken(Authentication authentication, long validityInMilliseconds) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        Claims claims = Jwts.claims().setSubject(principal.getUsername());
        claims.put("id", principal.getId());
        claims.put("authorities", principal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));

        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public Authentication getAuthentication(String token) {
        Claims claims = parseClaims(token);
        Collection<? extends GrantedAuthority> authorities = Arrays.stream(
                claims.get("authorities").toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        UserDetails principal = userDetailsService.loadUserByUsername(claims.getSubject());
        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException | ExpiredJwtException |
                 UnsupportedJwtException | IllegalArgumentException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }

    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
} 