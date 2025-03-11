package com.pyeon.domain.image.presentation;

import com.pyeon.domain.image.dto.response.PreSignedUrlRes;
import com.pyeon.domain.image.service.ImageService;
import com.pyeon.global.exception.ErrorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import static com.pyeon.global.exception.ErrorCode.EXCEED_IMAGE_SIZE;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/images")
public class ImageController {

    private final ImageService imageService;

    @PostMapping("/presigned-url")
    public ResponseEntity<PreSignedUrlRes> getPresignedUrl(@RequestHeader("Content-Type") String contentType) {
        PreSignedUrlRes presignedUrl = imageService.generatePresignedUrl(contentType);
        return ResponseEntity.ok(presignedUrl);
    }
    @DeleteMapping("/{fileName}")
    public ResponseEntity<Void> deleteImage(@PathVariable String fileName) {
        imageService.deleteImage(fileName);
        return ResponseEntity.noContent().build();
    }
    
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        return ResponseEntity
                .status(EXCEED_IMAGE_SIZE.getHttpStatus())
                .body(ErrorResponse.from(EXCEED_IMAGE_SIZE));
    }
} 