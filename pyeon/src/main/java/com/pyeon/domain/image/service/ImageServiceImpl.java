package com.pyeon.domain.image.service;

import com.pyeon.domain.image.domain.ImageFile;
import com.pyeon.domain.image.domain.S3ImageEvent;
import com.pyeon.domain.image.dto.response.ImageRes;
import com.pyeon.domain.image.infra.ImageUploader;
import com.pyeon.global.exception.ImageException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import static com.pyeon.global.exception.ErrorCode.EMPTY_IMAGE;
import static com.pyeon.global.exception.ErrorCode.EXCEED_IMAGE_SIZE;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    private final ImageUploader imageUploader;
    private final ApplicationEventPublisher publisher;

    @Override
    public ImageRes save(final MultipartFile image) {
        validateImage(image);
        final ImageFile imageFile = new ImageFile(image);
        final String imageUrl = uploadImage(imageFile);
        return new ImageRes(imageUrl);
    }

    private String uploadImage(final ImageFile imageFile) {
        try {
            return imageUploader.uploadImage(imageFile);
        } catch (final ImageException e) {
            publisher.publishEvent(new S3ImageEvent(imageFile.getHashedName()));
            throw e;
        }
    }

    private void validateImage(final MultipartFile image) {
        if (image == null || image.isEmpty()) {
            throw new ImageException(EMPTY_IMAGE);
        }
        
        if (image.getSize() > MAX_FILE_SIZE) {
            throw new ImageException(EXCEED_IMAGE_SIZE);
        }
    }
} 