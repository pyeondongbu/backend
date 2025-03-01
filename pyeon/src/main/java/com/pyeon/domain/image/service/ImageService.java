package com.pyeon.domain.image.service;

import com.pyeon.domain.image.dto.response.ImageRes;
import org.springframework.web.multipart.MultipartFile;

public interface ImageService {
    ImageRes save(final MultipartFile image);
} 