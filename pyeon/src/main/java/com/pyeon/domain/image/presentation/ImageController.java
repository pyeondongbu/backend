package com.pyeon.domain.image.presentation;

import com.pyeon.domain.image.dto.response.ImageRes;
import com.pyeon.domain.image.service.ImageService;
import com.pyeon.global.exception.ErrorResponse;
import com.pyeon.global.exception.ImageException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;

import static com.pyeon.global.exception.ErrorCode.EXCEED_IMAGE_SIZE;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/images")
public class ImageController {

    private final ImageService imageService;

    @PostMapping
    public ResponseEntity<ImageRes> uploadImage(@RequestPart(name = "image") MultipartFile image) {
        final ImageRes imageResponse = imageService.save(image);
        return ResponseEntity.created(URI.create(imageResponse.getImageUrl())).body(imageResponse);
    }
    
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        return ResponseEntity
                .status(EXCEED_IMAGE_SIZE.getHttpStatus())
                .body(ErrorResponse.from(EXCEED_IMAGE_SIZE));
    }
} 