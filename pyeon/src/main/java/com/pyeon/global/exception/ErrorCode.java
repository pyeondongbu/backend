package com.pyeon.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {

    INVALID_REQUEST(1000, "올바르지 않은 요청입니다."),
    NOT_FOUND(1001, "찾을 수 없는 페이지입니다");

    private final int status;
    private final String message;
}