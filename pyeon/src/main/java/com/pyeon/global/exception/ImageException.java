package com.pyeon.global.exception;

public class ImageException extends CustomException {
    
    public ImageException(ErrorCode errorCode) {
        super(errorCode);
    }
    
    public ImageException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
} 