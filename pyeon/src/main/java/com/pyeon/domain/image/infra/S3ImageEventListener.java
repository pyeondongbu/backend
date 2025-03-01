package com.pyeon.domain.image.infra;

import com.amazonaws.services.s3.AmazonS3;
import com.pyeon.domain.image.domain.S3ImageEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class S3ImageEventListener {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.s3.folder}")
    private String folder;

    @Async
    @EventListener
    public void handleS3ImageEvent(S3ImageEvent event) {
        try {
            final String imageName = event.getImageName();
            final String path = folder + imageName;
            
            if (amazonS3.doesObjectExist(bucket, path)) {
                amazonS3.deleteObject(bucket, path);
                log.info("S3 이미지 삭제 성공: {}", path);
            } else {
                log.warn("S3 이미지가 존재하지 않음: {}", path);
            }
        } catch (Exception e) {
            log.error("S3 이미지 삭제 실패: {}", e.getMessage(), e);
        }
    }
} 