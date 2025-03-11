package com.pyeon.domain.image.service;

import com.pyeon.domain.image.domain.S3ImageEvent;
import com.pyeon.domain.image.dto.response.PreSignedUrlRes;
import com.pyeon.global.config.S3Config;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

    private final S3Config s3Config;
    private final ApplicationEventPublisher publisher;

    @Override
    public PreSignedUrlRes generatePresignedUrl(String contentType) {
        String fileName = UUID.randomUUID().toString();
        String preSignedUrl = s3Config.generatePresignedUrl(fileName, contentType);
        return new PreSignedUrlRes(preSignedUrl, fileName);
    }

    public void deleteImage(String fileName) {
        publisher.publishEvent(new S3ImageEvent(fileName));
    }
} 