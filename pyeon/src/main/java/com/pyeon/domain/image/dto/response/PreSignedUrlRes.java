package com.pyeon.domain.image.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PreSignedUrlRes {
    private final String preSignedUrl;
    private final String fileName;
} 