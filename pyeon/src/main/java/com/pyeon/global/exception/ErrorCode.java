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
    TOKEN_MALFORMED(HttpStatus.UNAUTHORIZED, "AUTH_005", "잘못된 형식의 토큰입니다."),
    TOKEN_UNSUPPORTED(HttpStatus.UNAUTHORIZED, "AUTH_006", "지원하지 않는 토큰입니다."),
    TOKEN_SIGNATURE_INVALID(HttpStatus.UNAUTHORIZED, "AUTH_007", "토큰 서명이 유효하지 않습니다."),
    
    // Member 관련 에러
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER_001", "사용자를 찾을 수 없습니다."),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "MEMBER_002", "이미 존재하는 이메일입니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "MEMBER_003", "잘못된 비밀번호입니다."),
    MEMBER_SUSPENDED(HttpStatus.FORBIDDEN, "MEMBER_004", "일시 정지된 회원입니다."),
    MEMBER_BANNED(HttpStatus.FORBIDDEN, "MEMBER_005", "영구 정지된 회원입니다."),
    ADMIN_NOT_SUSPENDABLE(HttpStatus.FORBIDDEN, "MEMBER_006", "관리자는 제재할 수 없습니다."),
    MEMBER_DEACTIVATED(HttpStatus.FORBIDDEN, "MEMBER_007", "사용이 제한된 사용자이거나 탈퇴한 사용자입니다."),
    DUPLICATE_NICKNAME(HttpStatus.CONFLICT, "MEMBER_008", "이미 존재하는 닉네임입니다."),

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
    NOT_COMMENT_AUTHOR(HttpStatus.FORBIDDEN, "COMMENT_002", "댓글의 작성자가 아닙니다."),
    
    // Redis 관련 에러
    REDIS_CONNECTION_FAILED(HttpStatus.SERVICE_UNAVAILABLE, "REDIS_001", "Redis 서버 연결에 실패했습니다."),
    
    // 이미지 관련 에러
    NULL_IMAGE(HttpStatus.BAD_REQUEST, "IMG_001", "이미지가 없습니다."),
    EMPTY_IMAGE(HttpStatus.BAD_REQUEST, "IMG_002", "이미지가 비어있습니다."),
    INVALID_IMAGE(HttpStatus.BAD_REQUEST, "IMG_003", "유효하지 않은 이미지입니다."),
    INVALID_IMAGE_PATH(HttpStatus.BAD_REQUEST, "IMG_004", "유효하지 않은 이미지 경로입니다."),
    FAIL_IMAGE_NAME_HASH(HttpStatus.INTERNAL_SERVER_ERROR, "IMG_005", "이미지 이름 해싱에 실패했습니다."),
    EMPTY_IMAGE_LIST(HttpStatus.BAD_REQUEST, "IMG_006", "이미지 목록이 비어있습니다."),
    EXCEED_IMAGE_LIST_SIZE(HttpStatus.BAD_REQUEST, "IMG_007", "이미지 목록 크기를 초과했습니다."),
    EXCEED_IMAGE_SIZE(HttpStatus.BAD_REQUEST, "IMG_008", "이미지 크기가 5MB를 초과했습니다."),
    IMAGE_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "IMG_009", "이미지 삭제에 실패했습니다."),
    IMAGE_URL_PARSE_FAILED(HttpStatus.BAD_REQUEST, "IMG_010", "이미지 URL 파싱에 실패했습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}