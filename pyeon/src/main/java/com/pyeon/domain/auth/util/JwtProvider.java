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

/**
 * JWT 토큰 생성 및 검증을 담당하는 클래스
 * Access Token과 Refresh Token의 생성, 검증, 파싱 등을 처리
 */
@Component
@Slf4j
public class JwtProvider {

    private final String secretKey;
    private final long accessTokenValidityInMilliseconds;
    private final long refreshTokenValidityInMilliseconds;
    private final CustomUserDetailsService userDetailsService;

    /**
     * JWT Provider 생성자
     * 
     * @param secretKey JWT 서명에 사용할 비밀키
     * @param accessTokenValidityInMilliseconds Access Token 유효 시간(밀리초)
     * @param refreshTokenValidityInMilliseconds Refresh Token 유효 시간(밀리초)
     * @param userDetailsService 사용자 정보 조회 서비스
     */
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

    /**
     * Access Token 생성
     * 
     * @param authentication 인증 정보
     * @return 생성된 Access Token
     */
    public String createAccessToken(Authentication authentication) {
        return createToken(authentication, accessTokenValidityInMilliseconds);
    }

    /**
     * Refresh Token 생성
     * 
     * @param authentication 인증 정보
     * @return 생성된 Refresh Token
     */
    public String createRefreshToken(Authentication authentication) {
        return createToken(authentication, refreshTokenValidityInMilliseconds);
    }

    /**
     * JWT 토큰 생성 로직
     * 사용자 ID, 권한 정보를 포함한 토큰 생성
     * 
     * @param authentication 인증 정보
     * @param validityInMilliseconds 토큰 유효 시간
     * @return 생성된 JWT 토큰
     */
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

    /**
     * 토큰에서 인증 정보 추출
     * 토큰을 파싱하여 Spring Security Authentication 객체 생성
     * 
     * @param token JWT 토큰
     * @return Authentication 인증 객체
     */
    public Authentication getAuthentication(String token) {
        Claims claims = parseClaims(token);
        Collection<? extends GrantedAuthority> authorities = Arrays.stream(
                claims.get("authorities").toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        UserDetails principal = userDetailsService.loadUserByUsername(claims.getSubject());
        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    /**
     * 토큰 유효성 검증
     * 만료, 서명, 형식 등 검증
     * 
     * @param token 검증할 JWT 토큰
     * @return 토큰 유효성 여부
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.error("잘못된 JWT 서명입니다: {}", e.getMessage());
            return false;
        } catch (ExpiredJwtException e) {
            log.error("만료된 JWT 토큰입니다: {}", e.getMessage());
            return false;
        } catch (UnsupportedJwtException e) {
            log.error("지원되지 않는 JWT 토큰입니다: {}", e.getMessage());
            return false;
        } catch (IllegalArgumentException e) {
            log.error("JWT 토큰이 잘못되었습니다: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 토큰에서 Claims 추출
     * 
     * @param token JWT 토큰
     * @return 토큰의 Claims
     */
    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
} 