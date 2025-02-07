package com.pyeon.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {
    // Common
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "올바르지 않은 요청입니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "찾을 수 없는 페이지입니다."),
    
    // Member
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 회원입니다."),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "이미 존재하는 이메일입니다."),
    
    // Post
    POST_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 게시글입니다."),
    NOT_POST_AUTHOR(HttpStatus.FORBIDDEN, "게시글의 작성자가 아닙니다."),
    ALREADY_LIKED_POST(HttpStatus.CONFLICT, "이미 추천한 게시글입니다."),
    
    // Auth
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "만료된 토큰입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증되지 않은 요청입니다.");

    private final HttpStatus httpStatus;
    private final String message;
}