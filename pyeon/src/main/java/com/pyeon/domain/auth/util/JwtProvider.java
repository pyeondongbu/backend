package com.pyeon.domain.auth.util;

import com.pyeon.domain.auth.domain.UserPrincipal;
import com.pyeon.global.exception.CustomException;
import com.pyeon.global.exception.ErrorCode;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * JWT 토큰 생성 및 검증을 담당하는 클래스
 * Access Token과 Refresh Token의 생성, 검증, 파싱 등을 처리
 */
@Component
@Slf4j
public class JwtProvider {

    private final String secretKey;
    private final long tokenValidityInMilliseconds;

    /**
     * JWT Provider 생성자
     * 
     * @param secretKey JWT 서명에 사용할 비밀키
     * @param tokenValidityInMilliseconds Access Token 유효 시간(밀리초)
     */
    public JwtProvider(
            @Value("${jwt.secret-key}") String secretKey,
            @Value("${jwt.access-token-validity}") long tokenValidityInMilliseconds
    ) {
        this.secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
        this.tokenValidityInMilliseconds = tokenValidityInMilliseconds;
    }

    /**
     * Access Token 생성
     * 
     * @param userPrincipal 인증 정보
     * @return 생성된 Access Token
     */
    public String createAccessToken(UserPrincipal userPrincipal) {
        Claims claims = createClaims(userPrincipal);
        Date validity = createExpirationDate();

        return buildToken(claims, validity);
    }

    /**
     * Claims 생성
     */
    private Claims createClaims(UserPrincipal userPrincipal) {
        Claims claims = Jwts.claims();
        claims.setSubject(userPrincipal.getEmail());
        claims.put("id", userPrincipal.getId());
        claims.put("nickname", userPrincipal.getNickname());
        claims.put("profileImageUrl", userPrincipal.getProfileImageUrl());
        claims.put("authorities", extractAuthorities(userPrincipal));
        return claims;
    }

    /**
     * 권한 정보 추출
     */
    private List<String> extractAuthorities(UserPrincipal userPrincipal) {
        return userPrincipal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
    }

    /**
     * 토큰 만료 시간 생성
     */
    private Date createExpirationDate() {
        Date now = new Date();
        return new Date(now.getTime() + tokenValidityInMilliseconds);
    }

    /**
     * JWT 토큰 빌드
     */
    private String buildToken(Claims claims, Date validity) {
        Date now = new Date();
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
        UserPrincipal userPrincipal = buildUserPrincipalFromClaims(claims);
        Collection<? extends GrantedAuthority> authorities = extractAuthoritiesFromClaims(claims);

        return new UsernamePasswordAuthenticationToken(userPrincipal, "", authorities);
    }

    /**
     * Claims에서 사용자 권한 추출
     */
    private Collection<? extends GrantedAuthority> extractAuthoritiesFromClaims(Claims claims) {
        return ((List<?>) claims.get("authorities")).stream()
                .map(authority -> new SimpleGrantedAuthority((String) authority))
                .collect(Collectors.toList());
    }

    /**
     * Claims로부터 UserPrincipal 객체 생성
     */
    private UserPrincipal buildUserPrincipalFromClaims(Claims claims) {
        String email = claims.getSubject();
        Long id = claims.get("id", Long.class);
        String nickname = claims.get("nickname", String.class);
        String profileImageUrl = claims.get("profileImageUrl", String.class);

        return UserPrincipal.builder()
                .id(id)
                .email(email)
                .nickname(nickname)
                .profileImageUrl(profileImageUrl)
                .authorities(Collections.emptyList()) // 권한은 별도로 설정됨
                .build();
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
            Claims claims = parseClaims(token);
            return !claims.getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            throw new CustomException(ErrorCode.TOKEN_EXPIRED);
        } catch (MalformedJwtException e) {
            throw new CustomException(ErrorCode.TOKEN_MALFORMED);
        } catch (UnsupportedJwtException e) {
            throw new CustomException(ErrorCode.TOKEN_UNSUPPORTED);
        } catch (SecurityException | SignatureException e) {
            throw new CustomException(ErrorCode.TOKEN_SIGNATURE_INVALID);
        } catch (IllegalArgumentException e) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
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