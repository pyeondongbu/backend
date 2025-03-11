package com.pyeon.domain.image.service;

import com.pyeon.domain.image.dto.response.PreSignedUrlRes;

public interface ImageService {
    PreSignedUrlRes generatePresignedUrl(String contentType);
    void deleteImage(String fileName);
} 