package com.pyeon.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // Auth 관련 에러
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "AUTH_001", "인증이 필요합니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH_002", "유효하지 않은 토큰입니다."),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "AUTH_003", "만료된 토큰입니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "AUTH_004", "접근 권한이 없습니다."),
    
    // Member 관련 에러
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER_001", "사용자를 찾을 수 없습니다."),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "MEMBER_002", "이미 존재하는 이메일입니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "MEMBER_003", "잘못된 비밀번호입니다."),

    // Request 관련 에러
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "REQ_001", "잘못된 요청입니다."),
    MISSING_PARAMETER(HttpStatus.BAD_REQUEST, "REQ_002", "필수 파라미터가 누락되었습니다."),
    INVALID_FORMAT(HttpStatus.BAD_REQUEST, "REQ_003", "잘못된 형식입니다."),
    
    // Server 에러
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "SRV_001", "서버 에러가 발생했습니다."),
    DATABASE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "SRV_002", "데이터베이스 에러가 발생했습니다."),

    // OAuth2 관련 에러
    OAUTH2_REGISTRATION_NOT_FOUND(HttpStatus.BAD_REQUEST, "OAUTH2_001", "지원하지 않는 소셜 로그인입니다."),
    OAUTH2_EMAIL_NOT_VERIFIED(HttpStatus.BAD_REQUEST, "OAUTH2_002", "이메일 인증이 필요합니다."),
    OAUTH2_EMAIL_NOT_FOUND(HttpStatus.BAD_REQUEST, "OAUTH2_003", "이메일 정보가 없습니다."),
    OAUTH2_USER_INFO_NOT_FOUND(HttpStatus.BAD_REQUEST, "OAUTH2_004", "사용자 정보가 없습니다."),
    OAUTH2_PROCESSING_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "OAUTH2_005", "소셜 로그인 처리 중 오류가 발생했습니다."),

    // Post 관련 에러
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "POST_001", "게시글을 찾을 수 없습니다."),
    NOT_POST_AUTHOR(HttpStatus.FORBIDDEN, "POST_002", "게시글의 작성자가 아닙니다."),
    ALREADY_LIKED_POST(HttpStatus.CONFLICT, "POST_003", "이미 좋아요를 누른 게시글입니다."),

    // Comment 관련 에러
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "COMMENT_001", "댓글을 찾을 수 없습니다."),
    NOT_COMMENT_AUTHOR(HttpStatus.FORBIDDEN, "COMMENT_002", "댓글의 작성자가 아닙니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}