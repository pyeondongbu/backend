package com.pyeon.global.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ErrorResponse {
    private int status;
    private String message;
    private List<FieldError> errors;

    /**
     * 에러 코드만으로 ErrorResponse 생성
     */
    private ErrorResponse(final ErrorCode errorCode) {
        this.status = errorCode.getHttpStatus().value();
        this.message = errorCode.getMessage();
        this.errors = new ArrayList<>();
    }

    /**
     * 에러 코드와 FieldError 리스트로 ErrorResponse 생성
     */
    private ErrorResponse(final ErrorCode errorCode, final List<FieldError> errors) {
        this.status = errorCode.getHttpStatus().value();
        this.message = errorCode.getMessage();
        this.errors = errors;
    }

    /**
     * 에러 코드로 ErrorResponse 생성
     */
    public static ErrorResponse from(ErrorCode errorCode) {
        return new ErrorResponse(errorCode);
    }

    /**
     * 에러 코드와 BindingResult로 ErrorResponse 생성
     */
    public static ErrorResponse of(ErrorCode errorCode, BindingResult bindingResult) {
        return new ErrorResponse(errorCode, FieldError.from(bindingResult));
    }

    /**
     * 에러 코드와 메시지로 ErrorResponse 생성
     */
    public static ErrorResponse of(ErrorCode errorCode, String message) {
        ErrorResponse response = new ErrorResponse(errorCode);
        response.message = message;
        return response;
    }

    /**
     * API 검증 에러 정보를 담는 내부 클래스
     */
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class FieldError {
        private String field;      // 에러가 발생한 필드명
        private String value;      // 에러가 발생한 필드의 값
        private String reason;     // 에러 발생 이유

        private FieldError(final String field, final String value, final String reason) {
            this.field = field;
            this.value = value;
            this.reason = reason;
        }

        /**
         * BindingResult로부터 FieldError 리스트 생성
         */
        public static List<FieldError> from(final BindingResult bindingResult) {
            return bindingResult.getFieldErrors()
                    .stream()
                    .map(error -> {
                        String value;
                        if (error.getRejectedValue() == null) {
                            value = "";
                        } else {
                            value = error.getRejectedValue().toString();
                        }
                        
                        return new FieldError(
                                error.getField(),
                                value,
                                error.getDefaultMessage()
                        );
                    })
                    .collect(Collectors.toList());
        }
    }
} 