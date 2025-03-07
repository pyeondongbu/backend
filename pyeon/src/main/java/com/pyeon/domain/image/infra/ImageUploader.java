package com.pyeon.domain.image.infra;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import com.pyeon.domain.image.domain.ImageFile;
import com.pyeon.global.exception.ImageException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static com.pyeon.global.exception.ErrorCode.INVALID_IMAGE;
import static com.pyeon.global.exception.ErrorCode.INVALID_IMAGE_PATH;

@Component
@RequiredArgsConstructor
public class ImageUploader {

    private static final String CACHE_CONTROL_VALUE = "max-age=3153600";

    private final AmazonS3 s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.s3.folder}")
    private String folder;

    public String uploadImage(final ImageFile imageFile) {
        final String path = folder + imageFile.getHashedName();
        final ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(imageFile.getContentType());
        metadata.setContentLength(imageFile.getSize());
        metadata.setCacheControl(CACHE_CONTROL_VALUE);

        try (final InputStream inputStream = imageFile.getInputStream()) {
            s3Client.putObject(bucket, path, inputStream, metadata);
        } catch (final AmazonServiceException e) {
            throw new ImageException(INVALID_IMAGE_PATH);
        } catch (final IOException e) {
            throw new ImageException(INVALID_IMAGE);
        }

        URL fileUrl = s3Client.getUrl(bucket, path);
        return fileUrl.toString();
    }
} 